package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.PosTransactionRequest;
import com.lazis.lazissultanagung.dto.response.DonationDetailDto;
import com.lazis.lazissultanagung.dto.response.PosDashboardResponse;
import com.lazis.lazissultanagung.dto.response.PosHistoryResponse;
import com.lazis.lazissultanagung.dto.response.PosTransactionResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.*;
import com.lazis.lazissultanagung.repository.*;
import com.lazis.lazissultanagung.util.TerbilangUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosServiceImpl implements PosService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private ZakatRepository zakatRepository;

    @Autowired
    private InfakRepository infakRepository;

    @Autowired
    private DSKLRepository dsklRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private WakafRepository wakafRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TemporaryTransactionRepository temporaryTransactionRepository;

    @Autowired
    private AgenRepository agenRepository;

    @Override
    @Transactional
    public PosTransactionResponse createPosTransaction(PosTransactionRequest request, Long agenId) {
        // Validasi input
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Nominal donasi harus lebih dari 0");
        }
        if (request.getDate() == null) {
            throw new BadRequestException("Tanggal harus diisi");
        }
        if (request.getCategoryType() == null || request.getCategoryId() == null) {
            throw new BadRequestException("Kategori dan Sub Kategori harus diisi");
        }
        if (request.getEventId() == null) {
            throw new BadRequestException("Event ID harus diisi");
        }

        // Generate Nomor Bukti
        Integer lastTransactionNumber = transactionRepository.findLastTransactionNumber();
        int newTransactionNumber = (lastTransactionNumber == null ? 1 : lastTransactionNumber + 1);
        String transactionNumberFormatted = String.valueOf(newTransactionNumber);
        String staticPart = "LAZ";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        String nomorBukti = transactionNumberFormatted + "/" + staticPart + "/" + datePart;

        String imageFileName = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageFileName = fileStorageService.saveFile(request.getImage());
        }

        Coa coaDebit = null;
        Coa coaKredit = null;
        Object categoryEntity = null;
        String subCategoryName = "";

        // Tentukan COA berdasarkan kategori
        String categoryType = request.getCategoryType().toLowerCase();
        switch (categoryType) {
            case "zakat":
                Zakat zakat = zakatRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException("Zakat category not found"));
                coaDebit = zakat.getCoaDebit();
                coaKredit = zakat.getCoaKredit();
                categoryEntity = zakat;
                subCategoryName = zakat.getCategoryName();
                
                // Update current amount
                zakatRepository.updateZakatCurrentAmount(request.getCategoryId(), request.getAmount());
                break;
            case "infak":
                Infak infak = infakRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException("Infak category not found"));
                coaDebit = infak.getCoaDebit();
                coaKredit = infak.getCoaKredit();
                categoryEntity = infak;
                subCategoryName = infak.getCategoryName();
                
                // Update current amount
                infakRepository.updateInfakCurrentAmount(request.getCategoryId(), request.getAmount());
                break;
            case "dskl":
                DSKL dskl = dsklRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException("DSKL category not found"));
                coaDebit = dskl.getCoaDebit();
                coaKredit = dskl.getCoaKredit();
                categoryEntity = dskl;
                subCategoryName = dskl.getCategoryName();
                
                // Update current amount
                dsklRepository.updateDSKLCurrentAmount(request.getCategoryId(), request.getAmount());
                break;
            case "wakaf":
                Wakaf wakaf = wakafRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException("Wakaf category not found"));
                // Wakaf logic placeholder
                 throw new BadRequestException("Kategori Wakaf belum didukung di POS");
            case "campaign":
                Campaign campaign = campaignRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new BadRequestException("Campaign not found"));
                
                // Hardcode COA 20 (Debit) dan 21 (Credit)
                coaDebit = coaRepository.findById(20L).orElseThrow(() -> new BadRequestException("COA ID 20 not found"));
                coaKredit = coaRepository.findById(21L).orElseThrow(() -> new BadRequestException("COA ID 21 not found"));
                categoryEntity = campaign;
                subCategoryName = campaign.getCampaignName();
                
                // Update current amount
                campaignRepository.updateCampaignCurrentAmount(request.getCategoryId(), request.getAmount());
                break;
            default:
                throw new BadRequestException("Invalid category type: " + categoryType);
        }

        if (coaDebit == null || coaKredit == null) {
            throw new BadRequestException("COA Debit atau Kredit belum dikonfigurasi untuk kategori ini");
        }

        LocalDateTime transactionDateTime = request.getDate().atTime(LocalTime.now());

        String paymentMethod = request.getPaymentMethod();
        boolean isTemporary = paymentMethod != null && (paymentMethod.equalsIgnoreCase("transfer") || paymentMethod.equalsIgnoreCase("qris"));

        // Check for Transfer or QRIS payment method
        if (isTemporary) {
            // Generate temporary nomor bukti
            // Format: TMP-yyyyMMddHHmmss-Random3Digits
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String randomSuffix = String.format("%03d", (int) (Math.random() * 1000));
            String tempNomorBukti = "TMP-" + timestamp + "-" + randomSuffix;

            // Temporary Transaction Debit
            TemporaryTransaction tempDebit = new TemporaryTransaction();
            tempDebit.setNomorBukti(tempNomorBukti);
            tempDebit.setTransactionDate(transactionDateTime);
            tempDebit.setUsername(request.getName());
            tempDebit.setPhoneNumber(request.getPhoneNumber());
            tempDebit.setEmail(request.getEmail());
            tempDebit.setAddress(request.getAddress());
            tempDebit.setMessage(request.getDescription());
            tempDebit.setDebit(request.getAmount());
            tempDebit.setKredit(0.0);
            tempDebit.setCoa(coaDebit);
            tempDebit.setTransactionAmount(request.getAmount());
            tempDebit.setMethod(paymentMethod);
            tempDebit.setChannel("POS");
            tempDebit.setSuccess(true); // Or false if pending validation? User said same as transaction, usually true implies payment successful.
            tempDebit.setCategory(request.getCategoryType());
            tempDebit.setAgenId(agenId);
            tempDebit.setEventId(request.getEventId());
            tempDebit.setPaymentProofImage(imageFileName);
            setTemporaryTransactionCategory(tempDebit, categoryType, categoryEntity);
            temporaryTransactionRepository.save(tempDebit);

            // Temporary Transaction Credit
            TemporaryTransaction tempKredit = new TemporaryTransaction();
            tempKredit.setNomorBukti(tempNomorBukti);
            tempKredit.setTransactionDate(transactionDateTime);
            tempKredit.setUsername(request.getName());
            tempKredit.setPhoneNumber(request.getPhoneNumber());
            tempKredit.setEmail(request.getEmail());
            tempKredit.setAddress(request.getAddress());
            tempKredit.setMessage(request.getDescription());
            tempKredit.setDebit(0.0);
            tempKredit.setKredit(request.getAmount());
            tempKredit.setCoa(coaKredit);
            tempKredit.setTransactionAmount(request.getAmount());
            tempKredit.setMethod(paymentMethod);
            tempKredit.setChannel("POS");
            tempKredit.setSuccess(true);
            tempKredit.setCategory(request.getCategoryType());
            tempKredit.setAgenId(agenId);
            tempKredit.setEventId(request.getEventId());
            tempKredit.setPaymentProofImage(imageFileName);
            setTemporaryTransactionCategory(tempKredit, categoryType, categoryEntity);
            temporaryTransactionRepository.save(tempKredit);

            // Prepare Response (same as normal flow)
            PosTransactionResponse response = new PosTransactionResponse();
            response.setNomorBukti(tempNomorBukti);
            response.setTanggal(request.getDate());
            response.setNama(request.getName());
            response.setNoHp(request.getPhoneNumber());
            response.setEmail(request.getEmail());
            response.setAlamat(request.getAddress());
            
            List<DonationDetailDto> donationDetails = new ArrayList<>();
            String[] categories = {"zakat", "infak", "dskl", "campaign"};

            for (String cat : categories) {
                DonationDetailDto detail = new DonationDetailDto();
                detail.setKategori(cat);
                
                if (cat.equalsIgnoreCase(categoryType)) {
                    detail.setSubKategori(subCategoryName);
                    detail.setNominal(request.getAmount());
                } else {
                    detail.setSubKategori("");
                    detail.setNominal(0.0);
                }
                donationDetails.add(detail);
            }
            
            response.setDonasi(donationDetails);
            response.setTerbilang(TerbilangUtil.terbilang(request.getAmount()));
            
            return response;
        }

        // Buat Transaksi Debit
        Transaction transactionDebit = new Transaction();
        transactionDebit.setTransactionDate(transactionDateTime);
        transactionDebit.setUsername(request.getName());
        transactionDebit.setPhoneNumber(request.getPhoneNumber());
        transactionDebit.setEmail(request.getEmail());
        transactionDebit.setAddress(request.getAddress());
        transactionDebit.setMessage(request.getDescription());
        transactionDebit.setDebit(request.getAmount());
        transactionDebit.setKredit(0.0);
        transactionDebit.setCoa(coaDebit);
        transactionDebit.setNomorBukti(nomorBukti);
        transactionDebit.setTransactionAmount(request.getAmount());
        transactionDebit.setMethod(request.getPaymentMethod());
        transactionDebit.setChannel("POS");
        transactionDebit.setSuccess(true);
        transactionDebit.setCategory(request.getCategoryType());
        transactionDebit.setAgenId(agenId);
        transactionDebit.setEventId(request.getEventId());
        transactionDebit.setPaymentProofImage(imageFileName);
        
        // Set category relation
        setTransactionCategory(transactionDebit, categoryType, categoryEntity);
        
        transactionRepository.save(transactionDebit);

        // Buat Transaksi Kredit
        Transaction transactionKredit = new Transaction();
        transactionKredit.setTransactionDate(transactionDateTime);
        transactionKredit.setUsername(request.getName());
        transactionKredit.setPhoneNumber(request.getPhoneNumber());
        transactionKredit.setEmail(request.getEmail());
        transactionKredit.setAddress(request.getAddress());
        transactionKredit.setMessage(request.getDescription());
        transactionKredit.setDebit(0.0);
        transactionKredit.setKredit(request.getAmount());
        transactionKredit.setCoa(coaKredit);
        transactionKredit.setNomorBukti(nomorBukti);
        transactionKredit.setTransactionAmount(request.getAmount());
        transactionKredit.setMethod(request.getPaymentMethod());
        transactionKredit.setChannel("POS");
        transactionKredit.setSuccess(true);
        transactionKredit.setCategory(request.getCategoryType());
        transactionKredit.setAgenId(agenId);
        transactionKredit.setEventId(request.getEventId());
        transactionKredit.setPaymentProofImage(imageFileName);

        // Set category relation
        setTransactionCategory(transactionKredit, categoryType, categoryEntity);

        transactionRepository.save(transactionKredit);

        // Prepare Response
        PosTransactionResponse response = new PosTransactionResponse();
        response.setTanggal(request.getDate());
        response.setNama(request.getName());
        response.setNoHp(request.getPhoneNumber());
        response.setEmail(request.getEmail());
        response.setAlamat(request.getAddress());
        
        List<DonationDetailDto> donationDetails = new ArrayList<>();
        String[] categories = {"zakat", "infak", "dskl", "campaign"};

        for (String cat : categories) {
            DonationDetailDto detail = new DonationDetailDto();
            detail.setKategori(cat);
            
            if (cat.equalsIgnoreCase(categoryType)) {
                detail.setSubKategori(subCategoryName);
                detail.setNominal(request.getAmount());
            } else {
                detail.setSubKategori("");
                detail.setNominal(0.0);
            }
            donationDetails.add(detail);
        }
        
        response.setDonasi(donationDetails);
        response.setTerbilang(TerbilangUtil.terbilang(request.getAmount()));
        
        return response;
    }

    private void setTransactionCategory(Transaction transaction, String categoryType, Object categoryEntity) {
        switch (categoryType) {
            case "zakat":
                transaction.setZakat((Zakat) categoryEntity);
                break;
            case "infak":
                transaction.setInfak((Infak) categoryEntity);
                break;
            case "dskl":
                transaction.setDskl((DSKL) categoryEntity);
                break;
            case "campaign":
                transaction.setCampaign((Campaign) categoryEntity);
                break;
        }
    }

    private void setTemporaryTransactionCategory(TemporaryTransaction transaction, String categoryType, Object categoryEntity) {
        switch (categoryType) {
            case "zakat":
                transaction.setZakat((Zakat) categoryEntity);
                break;
            case "infak":
                transaction.setInfak((Infak) categoryEntity);
                break;
            case "dskl":
                transaction.setDskl((DSKL) categoryEntity);
                break;
            case "campaign":
                transaction.setCampaign((Campaign) categoryEntity);
                break;
        }
    }

    @Override
    public Page<PosHistoryResponse> getPosHistory(Long agenId, Long eventId, LocalDate startDate, LocalDate endDate, String category, String paymentMethod, Pageable pageable) {
        Specification<Transaction> spec = Specification.where(TransactionSpecification.isDebit())
                .and(TransactionSpecification.isNotPenyaluran())
                .and(TransactionSpecification.hasAgenId(agenId))
                .and(TransactionSpecification.hasChannel("POS"));

        if (eventId != null) {
            spec = spec.and(TransactionSpecification.hasEventId(eventId));
        }
        if (startDate != null && endDate != null) {
            spec = spec.and(TransactionSpecification.transactionDateBetween(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }
        if (category != null && !category.isEmpty()) {
            spec = spec.and(TransactionSpecification.hasCategory(category));
        }
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            spec = spec.and(TransactionSpecification.hasPaymentMethod(paymentMethod));
        }

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);

        return transactions.map(this::mapTransactionToHistoryResponse);
    }

    @Override
    public PosDashboardResponse getPosDashboard(Long agenId) {
        // Category Summary
        List<Object[]> categoryData = transactionRepository.getCategorySummaryByAgenId(agenId);
        
        List<PosDashboardResponse.CategoryNominalSummary> categoryNominalSummaries = categoryData.stream()
                .map(obj -> new PosDashboardResponse.CategoryNominalSummary(
                        (String) obj[0],
                        (Double) obj[1]
                ))
                .collect(Collectors.toList());

        List<PosDashboardResponse.CategoryCountSummary> categoryCountSummaries = categoryData.stream()
                .map(obj -> new PosDashboardResponse.CategoryCountSummary(
                        (String) obj[0],
                        (Long) obj[2]
                ))
                .collect(Collectors.toList());

        // Payment Method Summary
        List<Object[]> paymentData = transactionRepository.getPaymentMethodSummaryByAgenId(agenId);
        List<PosDashboardResponse.PaymentMethodSummary> paymentSummaries = paymentData.stream()
                .map(obj -> new PosDashboardResponse.PaymentMethodSummary(
                        (String) obj[0],
                        (Long) obj[1]
                ))
                .collect(Collectors.toList());

        // Event Summary
        List<Object[]> eventData = transactionRepository.getEventSummaryByAgenId(agenId);
        List<PosDashboardResponse.EventSummary> eventSummaries = eventData.stream()
                .map(obj -> {
                    Long eventId = (Long) obj[0];
                    Double totalNominal = (Double) obj[1];
                    String eventName = "Unknown Event";
                    if (eventId != null) {
                        eventName = eventRepository.findById(eventId)
                                .map(Event::getName)
                                .orElse("Unknown Event");
                    }
                    return new PosDashboardResponse.EventSummary(eventName, totalNominal);
                })
                .collect(Collectors.toList());

        // Target Summary
        Double currentTotal = transactionRepository.getTotalDonationByAgenId(agenId);
        Double target = 0.0;
        Double percentage = 0.0;
        
        com.lazis.lazissultanagung.model.Agen agen = agenRepository.findById(agenId).orElse(null);
        if (agen != null && agen.getTargetAmount() != null) {
            target = agen.getTargetAmount();
        }
        
        if (target > 0) {
            percentage = (currentTotal / target) * 100;
        }
        
        PosDashboardResponse.TargetSummary targetSummary = new PosDashboardResponse.TargetSummary(target, currentTotal, percentage);

        // Total Amount Today
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        Double totalAmountToday = transactionRepository.getTotalDonationByAgenIdAndDateRange(agenId, startOfDay, endOfDay);

        return new PosDashboardResponse(categoryNominalSummaries, categoryCountSummaries, paymentSummaries, eventSummaries, targetSummary, totalAmountToday);
    }

    private PosHistoryResponse mapTransactionToHistoryResponse(Transaction transaction) {
        PosHistoryResponse response = new PosHistoryResponse();
        response.setId(transaction.getTransactionId());
        response.setTanggal(transaction.getTransactionDate());
        response.setNomorBukti(transaction.getNomorBukti());
        response.setNama(transaction.getUsername());
        response.setNoHp(transaction.getPhoneNumber());
        response.setEmail(transaction.getEmail());
        response.setAlamat(transaction.getAddress());
        response.setKategori(transaction.getCategory());
        
        String subCategoryName = "";
        if (transaction.getZakat() != null) subCategoryName = transaction.getZakat().getCategoryName();
        else if (transaction.getInfak() != null) subCategoryName = transaction.getInfak().getCategoryName();
        else if (transaction.getDskl() != null) subCategoryName = transaction.getDskl().getCategoryName();
        else if (transaction.getCampaign() != null) subCategoryName = transaction.getCampaign().getCampaignName();
        response.setSubKategori(subCategoryName);
        
        response.setNominal(transaction.getDebit());
        response.setMetodePembayaran(transaction.getMethod());
        response.setPaymentProofImage(transaction.getPaymentProofImage());
        
        if (transaction.getEventId() != null) {
            eventRepository.findById(transaction.getEventId()).ifPresent(event -> {
                response.setNamaEvent(event.getName());
                response.setLokasiEvent(event.getLocation());
            });
        }
        
        return response;
    }

    private PosTransactionResponse mapTransactionToResponse(Transaction transaction) {
        PosTransactionResponse response = new PosTransactionResponse();
        response.setTanggal(transaction.getTransactionDate().toLocalDate());
        response.setNama(transaction.getUsername());
        response.setNoHp(transaction.getPhoneNumber());
        response.setEmail(transaction.getEmail());
        response.setAlamat(transaction.getAddress());
        
        String categoryType = transaction.getCategory();
        String subCategoryName = "";
        
        if (transaction.getZakat() != null) subCategoryName = transaction.getZakat().getCategoryName();
        else if (transaction.getInfak() != null) subCategoryName = transaction.getInfak().getCategoryName();
        else if (transaction.getDskl() != null) subCategoryName = transaction.getDskl().getCategoryName();
        else if (transaction.getCampaign() != null) subCategoryName = transaction.getCampaign().getCampaignName();
        
        List<DonationDetailDto> donationDetails = new ArrayList<>();
        String[] categories = {"zakat", "infak", "dskl", "campaign"};

        for (String cat : categories) {
            DonationDetailDto detail = new DonationDetailDto();
            detail.setKategori(cat);
            
            if (cat.equalsIgnoreCase(categoryType)) {
                detail.setSubKategori(subCategoryName);
                detail.setNominal(transaction.getDebit());
            } else {
                detail.setSubKategori("");
                detail.setNominal(0.0);
            }
            donationDetails.add(detail);
        }
        
        response.setDonasi(donationDetails);
        response.setTerbilang(TerbilangUtil.terbilang(transaction.getDebit()));
        
        return response;
    }
}
