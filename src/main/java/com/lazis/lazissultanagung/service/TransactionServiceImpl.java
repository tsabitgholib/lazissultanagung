package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.JurnalUmumRequest;
import com.lazis.lazissultanagung.dto.response.PosHistoryResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.dto.response.TransactionResponse;
import com.lazis.lazissultanagung.dto.response.DonaturTransactionsHistoryResponse;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.*;
import com.lazis.lazissultanagung.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ZakatRepository zakatRepository;

    @Autowired
    private InfakRepository infakRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private WakafRepository wakafRepository;

    @Autowired
    private DSKLRepository dsklRepository;

    @Autowired
    private DonaturRepository donaturRepository;

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private CoaRepository coaRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TemporaryTransactionRepository temporaryTransactionRepository;

    @Override
    public Page<TransactionResponse> getAllTransaction(Integer month, Integer year, Pageable pageable) {
        return transactionRepository.findAllByMonthAndYear(month, year, pageable)
                .map(transaction -> {
                    Object categoryData = getCategoryData(transaction);
                    return new TransactionResponse(transaction, categoryData);
                });
    }

//    @Override
//    public TransactionResponse createTransactionOFF(String categoryType, Long id, TransactionRequest transactionRequest) throws BadRequestException {
//        if (transactionRequest.getUsername() == null || transactionRequest.getPhoneNumber() == null) {
//            throw new BadRequestException("Username atau nomor handphone tidak boleh kosong");
//        }
//
//        // Dapatkan nomor transaksi terakhir
//        Integer lastTransactionNumber = transactionRepository.findLastTransactionNumber();  // Buat repository method untuk ini
//        int newTransactionNumber = (lastTransactionNumber == null ? 1 : lastTransactionNumber + 1);  // Auto increment
//
//        // Format nomor bukti
//        String transactionNumberFormatted = String.format("%03d", newTransactionNumber);  // Format angka menjadi 001, 002, dst.
//        String staticPart = "LAZ";
//        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));  // Format MM/yyyy untuk bulan dan tahun
//        String nomorBukti = transactionNumberFormatted + "/" + staticPart + "/" + datePart;
//
//        // Transaksi Debit
//        Transaction transactionDebit = new Transaction();
//        transactionDebit.setUsername(transactionRequest.getUsername());
//        transactionDebit.setPhoneNumber(transactionRequest.getPhoneNumber());
//        transactionDebit.setEmail(transactionRequest.getEmail());
//        transactionDebit.setTransactionAmount(transactionRequest.getTransactionAmount());
//        transactionDebit.setMessage(transactionRequest.getMessage());
//        transactionDebit.setDebit(transactionRequest.getTransactionAmount());
//        transactionDebit.setKredit(0.0);  // Kredit untuk transaksi debit diatur ke 0
//        transactionDebit.setNomorBukti(nomorBukti);
//
//        // Transaksi Kredit
//        Transaction transactionKredit = new Transaction();
//        transactionKredit.setUsername(transactionRequest.getUsername());
//        transactionKredit.setPhoneNumber(transactionRequest.getPhoneNumber());
//        transactionKredit.setEmail(transactionRequest.getEmail());
//        transactionKredit.setTransactionAmount(transactionRequest.getTransactionAmount());
//        transactionKredit.setMessage(transactionRequest.getMessage());
//        transactionKredit.setKredit(transactionRequest.getTransactionAmount());
//        transactionKredit.setDebit(0.0);  // Debit untuk transaksi kredit diatur ke 0
//        transactionKredit.setNomorBukti(nomorBukti);
//
//        // Set COA berdasarkan jenis transaksi dan tipe debit/kredit
//        Coa debitCoa;
//        Coa kreditCoa;
//
//        switch (categoryType) {
//            case "campaign":
//            case "infak":
//                debitCoa = coaRepository.findById(8L)
//                        .orElseThrow(() -> new RuntimeException("COA for debit campaign/infak not found"));
//                kreditCoa = coaRepository.findById(73L)
//                        .orElseThrow(() -> new RuntimeException("COA for kredit campaign/infak not found"));
//                break;
//            case "zakat":
//                debitCoa = coaRepository.findById(7L)
//                        .orElseThrow(() -> new RuntimeException("COA for debit zakat not found"));
//                kreditCoa = coaRepository.findById(54L)
//                        .orElseThrow(() -> new RuntimeException("COA for kredit zakat not found"));
//                break;
//            case "dskl":
//            case "wakaf":
//                debitCoa = coaRepository.findById(9L)
//                        .orElseThrow(() -> new RuntimeException("COA for debit dskl/wakaf not found"));
//                kreditCoa = coaRepository.findById(99L)
//                        .orElseThrow(() -> new RuntimeException("COA for kredit dskl/wakaf not found"));
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid category type: " + categoryType);
//        }
//
//        // Set COA pada transaksi debit dan kredit
//        transactionDebit.setCoa(debitCoa);
//        transactionKredit.setCoa(kreditCoa);
//
//        Object responseDto = null;
//        switch (categoryType) {
//            case "campaign":
//                Campaign campaign = campaignRepository.findById(id)
//                        .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
//                transactionDebit.setCampaign(campaign);
//                transactionKredit.setCampaign(campaign);
//                responseDto = modelMapper.map(campaign, CampaignResponse.class);
//                break;
//            case "zakat":
//                Zakat zakat = zakatRepository.findById(id)
//                        .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
//                transactionDebit.setZakat(zakat);
//                transactionKredit.setZakat(zakat);
//                responseDto = modelMapper.map(zakat, Zakat.class);
//                break;
//            case "infak":
//                Infak infak = infakRepository.findById(id)
//                        .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
//                transactionDebit.setInfak(infak);
//                transactionKredit.setInfak(infak);
//                responseDto = modelMapper.map(infak, Infak.class);
//                break;
//            case "dskl":
//                DSKL dskl = dsklRepository.findById(id)
//                        .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
//                transactionDebit.setDskl(dskl);
//                transactionKredit.setDskl(dskl);
//                responseDto = modelMapper.map(dskl, DSKL.class);
//                break;
//            case "wakaf":
//                Wakaf wakaf = wakafRepository.findById(id)
//                        .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
//                transactionDebit.setWakaf(wakaf);
//                transactionKredit.setWakaf(wakaf);
//                responseDto = modelMapper.map(wakaf, Wakaf.class);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid category type: " + categoryType);
//        }
//
//        // Set common properties
//        transactionDebit.setTransactionDate(LocalDateTime.now());
//        transactionDebit.setCategory(categoryType);
//        transactionDebit.setMethod("OFFLINE");
//        transactionDebit.setChannel("OFFLINE");
//        transactionDebit.setVaNumber("9876547894321567");
//        transactionDebit.setSuccess(true);
//
//        transactionKredit.setTransactionDate(LocalDateTime.now());
//        transactionKredit.setCategory(categoryType);
//        transactionKredit.setMethod("OFFLINE");
//        transactionKredit.setChannel("OFFLINE");
//        transactionKredit.setVaNumber("9876547894321567");
//        transactionKredit.setSuccess(true);
//
//        // Simpan kedua transaksi ke database
//        transactionRepository.save(transactionDebit);
//        transactionRepository.save(transactionKredit);
//
//        // Update current amount untuk campaign, zakat, infak, dskl, atau wakaf
//        switch (categoryType) {
//            case "campaign":
//                campaignRepository.updateCampaignCurrentAmount(id, transactionRequest.getTransactionAmount());
//
//                Messages messages = new Messages();
//                messages.setUsername(transactionDebit.getUsername());
//                messages.setMessagesDate(transactionDebit.getTransactionDate());
//                messages.setMessages(transactionDebit.getMessage());
//                messages.setCampaign(transactionDebit.getCampaign());
//                messages.setAamiin(messages.getAamiin() + 1);
//                messagesRepository.save(messages);
//                break;
//            case "zakat":
//                zakatRepository.updateZakatCurrentAmount(id, transactionRequest.getTransactionAmount());
//                break;
//            case "infak":
//                infakRepository.updateInfakCurrentAmount(id, transactionRequest.getTransactionAmount());
//                break;
//            case "dskl":
//                dsklRepository.updateDSKLCurrentAmount(id, transactionRequest.getTransactionAmount());
//                break;
//            case "wakaf":
//                wakafRepository.updateWakafCurrentAmount(id, transactionRequest.getTransactionAmount());
//                break;
//        }
//
//        return new TransactionResponse(transactionDebit, responseDto);  // Response menggunakan transaksi debit
//    }

    @Override
    public ResponseMessage createJurnalUmum(JurnalUmumRequest jurnalUmumRequest) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.KEUANGAN)) {
                throw new BadRequestException("Hanya Admin dan Admin Keuangan yang dapat membuat jurnal umum");
            }

            // Validasi input
            if (jurnalUmumRequest.getTransactionDate() == null ||
                    jurnalUmumRequest.getDescription() == null ||
                    jurnalUmumRequest.getCategoryType() == null ||
                    jurnalUmumRequest.getCategoryId() == null ||
                    jurnalUmumRequest.getDebitDetails() == null ||
                    jurnalUmumRequest.getKreditDetails() == null) {
                throw new BadRequestException("Semua field wajib diisi");
            }

            // Hitung nominal debet dan kredit dari detail
            Double nominalDebet = jurnalUmumRequest.getDebitDetails().stream()
                    .mapToDouble(JurnalUmumRequest.DebitDetail::getAmount)
                    .sum();

            Double nominalKredit = jurnalUmumRequest.getKreditDetails().stream()
                    .mapToDouble(JurnalUmumRequest.KreditDetail::getAmount)
                    .sum();

            // Dapatkan nomor transaksi terakhir
            Integer lastTransactionNumber = transactionRepository.findLastTransactionNumber(); // Repository method
            int newTransactionNumber = (lastTransactionNumber == null ? 1 : lastTransactionNumber + 1);

            // Format nomor bukti
            String transactionNumberFormatted = String.valueOf(newTransactionNumber);
            String staticPart = "LAZ";
            String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
            String nomorBukti = transactionNumberFormatted + "/" + staticPart + "/" + datePart;

            boolean isPenyaluran = jurnalUmumRequest.isPenyaluran() ||
                    jurnalUmumRequest.getCategoryType().equalsIgnoreCase("pengelola");

            // Transaksi Debit
            Transaction transactionDebit = new Transaction();
            transactionDebit.setTransactionDate(jurnalUmumRequest.getTransactionDate().atStartOfDay());
            transactionDebit.setUsername("Teller Manual");
            transactionDebit.setMessage(jurnalUmumRequest.getDescription());
            transactionDebit.setDebit(nominalDebet);
            transactionDebit.setKredit(0.0);
            transactionDebit.setCoa(coaRepository.findById(jurnalUmumRequest.getDebitDetails().get(0).getCoaId())
                    .orElseThrow(() -> new BadRequestException("COA Debet tidak ditemukan")));
            transactionDebit.setNomorBukti(nomorBukti);
            transactionDebit.setTransactionAmount(nominalDebet);
            transactionDebit.setMethod("OFFLINE");
            transactionDebit.setChannel("Teller");
            transactionDebit.setPenyaluran(isPenyaluran);
            transactionDebit.setSuccess(true);
            transactionDebit.setCategory(jurnalUmumRequest.getCategoryType());

            switch (jurnalUmumRequest.getCategoryType().toLowerCase()) {
                case "campaign":
                    Campaign campaign = campaignRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
                    transactionDebit.setCampaign(campaign);
                    break;
                case "zakat":
                    Zakat zakat = zakatRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
                    transactionDebit.setZakat(zakat);
                    break;
                case "infak":
                    Infak infak = infakRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
                    transactionDebit.setInfak(infak);
                    break;
                case "dskl":
                    DSKL dskl = dsklRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
                    transactionDebit.setDskl(dskl);
                    break;
                case "wakaf":
                    Wakaf wakaf = wakafRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
                    transactionDebit.setWakaf(wakaf);
                    break;
                case "pengelola":
                    break;
                default:
                    throw new BadRequestException("Invalid category type: " + jurnalUmumRequest.getCategoryType().toLowerCase()); // Exception handling update
            }

            // Simpan transaksi debit
            transactionRepository.save(transactionDebit);

            // Transaksi Kredit
            for (JurnalUmumRequest.KreditDetail kreditDetail : jurnalUmumRequest.getKreditDetails()) {
                Transaction transactionKredit = new Transaction();
                transactionKredit.setTransactionDate(jurnalUmumRequest.getTransactionDate().atStartOfDay());
                transactionKredit.setUsername("Teller Manual");
                transactionKredit.setMessage(jurnalUmumRequest.getDescription());
                transactionKredit.setDebit(0.0);
                transactionKredit.setKredit(kreditDetail.getAmount());
                transactionKredit.setCoa(coaRepository.findById(kreditDetail.getCoaId())
                        .orElseThrow(() -> new BadRequestException("COA Kredit tidak ditemukan")));
                transactionKredit.setNomorBukti(nomorBukti);
                transactionKredit.setTransactionAmount(nominalDebet);
                transactionKredit.setMethod("OFFLINE");
                transactionKredit.setChannel("Teller");
                transactionKredit.setPenyaluran(isPenyaluran);
                transactionKredit.setSuccess(true);
                transactionKredit.setCategory(jurnalUmumRequest.getCategoryType());

                switch (jurnalUmumRequest.getCategoryType().toLowerCase()) {
                    case "campaign":
                        Campaign campaign = campaignRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
                        transactionKredit.setCampaign(campaign);
                        break;
                    case "zakat":
                        Zakat zakat = zakatRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
                        transactionKredit.setZakat(zakat);
                        break;
                    case "infak":
                        Infak infak = infakRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
                        transactionKredit.setInfak(infak);
                        break;
                    case "dskl":
                        DSKL dskl = dsklRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
                        transactionKredit.setDskl(dskl);
                        break;
                    case "wakaf":
                        Wakaf wakaf = wakafRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
                        transactionKredit.setWakaf(wakaf);
                        break;
                    case "pengelola":
                        break;
                    default:
                        throw new BadRequestException("Invalid category type: " + jurnalUmumRequest.getCategoryType().toLowerCase()); // Exception handling update
                }

                // Simpan transaksi kredit
                transactionRepository.save(transactionKredit);
            }

            // Update current amount untuk kategori yang sesuai jika bukan penyaluran
            if (!jurnalUmumRequest.isPenyaluran()) {
                switch (jurnalUmumRequest.getCategoryType().toLowerCase()) {
                    case "campaign":
                        campaignRepository.updateCampaignCurrentAmount(jurnalUmumRequest.getCategoryId(), nominalDebet);
                        break;
                    case "zakat":
                        zakatRepository.updateZakatCurrentAmount(jurnalUmumRequest.getCategoryId(), nominalDebet);
                        break;
                    case "infak":
                        infakRepository.updateInfakCurrentAmount(jurnalUmumRequest.getCategoryId(), nominalDebet);
                        break;
                    case "wakaf":
                        wakafRepository.updateWakafCurrentAmount(jurnalUmumRequest.getCategoryId(), nominalDebet);
                        break;
                    case "dskl":
                        dsklRepository.updateDSKLCurrentAmount(jurnalUmumRequest.getCategoryId(), nominalDebet);
                        break;
                }
            }
            return new ResponseMessage(true, "Jurnal umum berhasil dibuat");
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    @Transactional
    public ResponseMessage validateTemporaryTransaction(String nomorBukti) {
        List<TemporaryTransaction> tempTransactions = temporaryTransactionRepository.findByNomorBukti(nomorBukti);
        if (tempTransactions.isEmpty()) {
            throw new BadRequestException("Temporary transaction not found");
        }

        // Generate Nomor Bukti (Reused logic)
        Integer lastTransactionNumber = transactionRepository.findLastTransactionNumber();
        int newTransactionNumber = (lastTransactionNumber == null ? 1 : lastTransactionNumber + 1);
        String transactionNumberFormatted = String.valueOf(newTransactionNumber);
        String staticPart = "LAZ";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        String newNomorBukti = transactionNumberFormatted + "/" + staticPart + "/" + datePart;

        // Use a set to track processed categories to avoid double counting (since temp transactions come in pairs)
        Set<String> processedCategories = new HashSet<>();

        for (TemporaryTransaction temp : tempTransactions) {
            Transaction transaction = new Transaction();
            transaction.setTransactionDate(temp.getTransactionDate());
            transaction.setUsername(temp.getUsername());
            transaction.setPhoneNumber(temp.getPhoneNumber());
            transaction.setEmail(temp.getEmail());
            transaction.setAddress(temp.getAddress());
            transaction.setTransactionAmount(temp.getTransactionAmount());
            transaction.setMessage(temp.getMessage());
            transaction.setPaymentProofImage(temp.getPaymentProofImage());
            transaction.setChannel(temp.getChannel());
            transaction.setVaNumber(temp.getVaNumber());
            transaction.setRefNo(temp.getRefNo());
            transaction.setMethod(temp.getMethod());
            transaction.setPrefix(temp.getPrefix());
            transaction.setWorkstation(temp.getWorkstation());
            transaction.setTransactionQrId(temp.getTransactionQrId());
            transaction.setSuccess(temp.isSuccess());
            transaction.setCategory(temp.getCategory());
            transaction.setCoa(temp.getCoa());
            transaction.setDebit(temp.getDebit());
            transaction.setKredit(temp.getKredit());
            transaction.setNomorBukti(newNomorBukti); // New Nomor Bukti
            transaction.setPenyaluran(temp.isPenyaluran());
            transaction.setCampaign(temp.getCampaign());
            transaction.setZakat(temp.getZakat());
            transaction.setInfak(temp.getInfak());
            transaction.setDskl(temp.getDskl());
            transaction.setWakaf(temp.getWakaf());
            transaction.setAgenId(temp.getAgenId());
            transaction.setEventId(temp.getEventId());
            transaction.setDonatur(temp.getDonatur());

            transactionRepository.save(transaction);
            
            // Update Category Amount logic
            // We only update once per transaction pair (usually identified by non-zero debit or credit, or just once per unique category entity ID)
            // Since PosServiceImpl updates amount regardless of Debit/Credit, we should be careful.
            // PosServiceImpl updates ONCE per request. Here we have 2 records (Debit/Credit).
            // Both have the same transactionAmount.
            // We should update only once.
            // Let's check the Debit record (usually where money comes IN to the system account, wait.
            // POS: Debit = Cash/Bank, Credit = Revenue (Zakat/Infak).
            // Usually revenue recognition is on Credit side.
            // Let's stick to updating once.
            
            if (!processedCategories.contains(newNomorBukti)) {
                 updateCategoryAmount(temp);
                 processedCategories.add(newNomorBukti);
            }
        }

        temporaryTransactionRepository.deleteAll(tempTransactions);

        return new ResponseMessage(true, "Transaction validated successfully");
    }

    private void updateCategoryAmount(TemporaryTransaction temp) {
        String categoryType = temp.getCategory();
        if (categoryType == null) return;
        
        switch (categoryType.toLowerCase()) {
            case "zakat":
                if (temp.getZakat() != null) {
                    zakatRepository.updateZakatCurrentAmount(temp.getZakat().getId(), temp.getTransactionAmount());
                }
                break;
            case "infak":
                if (temp.getInfak() != null) {
                    infakRepository.updateInfakCurrentAmount(temp.getInfak().getId(), temp.getTransactionAmount());
                }
                break;
            case "dskl":
                if (temp.getDskl() != null) {
                    dsklRepository.updateDSKLCurrentAmount(temp.getDskl().getId(), temp.getTransactionAmount());
                }
                break;
            case "campaign":
                if (temp.getCampaign() != null) {
                    campaignRepository.updateCampaignCurrentAmount(temp.getCampaign().getCampaignId(), temp.getTransactionAmount());
                }
                break;
        }
    }


    @Override
    public List<PosHistoryResponse> getAllTemporaryTransactions() {
        List<TemporaryTransaction> transactions = temporaryTransactionRepository.findAll();

        return transactions.stream().map(temp -> {
            PosHistoryResponse response = new PosHistoryResponse();
            response.setId(temp.getId());
            response.setTanggal(temp.getTransactionDate());
            response.setNomorBukti(temp.getNomorBukti());
            response.setNama(temp.getUsername());
            response.setNoHp(temp.getPhoneNumber());
            response.setEmail(temp.getEmail());
            response.setAlamat(temp.getAddress());
            response.setKategori(temp.getCategory());

            String subKategori = "-";
            if (temp.getCampaign() != null) {
                subKategori = temp.getCampaign().getCampaignName();
            } else if (temp.getZakat() != null) {
                subKategori = temp.getZakat().getCategoryName();
            } else if (temp.getInfak() != null) {
                subKategori = temp.getInfak().getCategoryName();
            } else if (temp.getDskl() != null) {
                subKategori = temp.getDskl().getCategoryName();
            } else if (temp.getWakaf() != null) {
                subKategori = temp.getWakaf().getCategoryName();
            }
            response.setSubKategori(subKategori);

            response.setNominal(temp.getTransactionAmount());
            response.setMetodePembayaran(temp.getMethod());
            response.setPaymentProofImage(temp.getPaymentProofImage());

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<TransactionResponse> getTransactionsByCampaignId(Long campaignId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByCampaignId(campaignId, pageable);

        return transactions.map(transaction -> new TransactionResponse(transaction, getCategoryData(transaction)));
    }

    @Override
    public Page<TransactionResponse> getTransactionsByZakatId(Long zakatId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByZakatId(zakatId, pageable);

        return transactions.map(transaction -> new TransactionResponse(transaction, getCategoryData(transaction)));
    }

    @Override
    public Page<TransactionResponse> getTransactionsByInfakId(Long infakId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByInfakId(infakId, pageable);

        return transactions.map(transaction -> new TransactionResponse(transaction, getCategoryData(transaction)));
    }

    @Override
    public Page<TransactionResponse> getTransactionsByDSKLId(Long dsklId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByDSKLId(dsklId, pageable);

        return transactions.map(transaction -> new TransactionResponse(transaction, getCategoryData(transaction)));
    }

    @Override
    public Page<TransactionResponse> getTransactionsByWakafId(Long wakafId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByWakafId(wakafId, pageable);

        return transactions.map(transaction -> new TransactionResponse(transaction, getCategoryData(transaction)));
    }

    @Override
    public List<DonaturTransactionsHistoryResponse> getDonaturTransactionsHistory() throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Ambil nomor telepon atau email dari user yang login
            String phoneNumber = userDetails.getPhoneNumber();
            String email = userDetails.getEmail();

            List<Transaction> donaturTransactions;

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                // Cari transaksi berdasarkan nomor telepon jika tersedia
                donaturTransactions = transactionRepository.findByPhoneNumber(phoneNumber);
            } else {
                // Jika nomor telepon tidak ada, cari berdasarkan email
                donaturTransactions = transactionRepository.findByEmail(email);
            }

            // Konversi transaksi ke DTO hanya jika debit > 0
            List<DonaturTransactionsHistoryResponse> donaturTransactionsHistory = new ArrayList<>();
            for (Transaction transaction : donaturTransactions) {
                if (transaction.getDebit() > 0) { // Filter hanya transaksi dengan debit > 0
                    DonaturTransactionsHistoryResponse transactionDTO = new DonaturTransactionsHistoryResponse();
                    transactionDTO.setUsername(transaction.getUsername());
                    transactionDTO.setTransactionAmount(transaction.getTransactionAmount());
                    transactionDTO.setMessage(transaction.getMessage());
                    transactionDTO.setTransactionDate(transaction.getTransactionDate());
                    transactionDTO.setSuccess(transaction.isSuccess());

                    // Tentukan tipe transaksi dan nama transaksinya
                    Object categoryData = getCategoryData(transaction);
                    if (categoryData != null) {
                        if (categoryData instanceof Campaign) {
                            transactionDTO.setCategory("Campaign");
                            transactionDTO.setTransactionName(((Campaign) categoryData).getCampaignName());
                        } else if (categoryData instanceof Zakat) {
                            transactionDTO.setCategory("Zakat");
                            transactionDTO.setTransactionName(((Zakat) categoryData).getCategoryName());
                        } else if (categoryData instanceof Infak) {
                            transactionDTO.setCategory("Infak");
                            transactionDTO.setTransactionName(((Infak) categoryData).getCategoryName());
                        } else if (categoryData instanceof Wakaf) {
                            transactionDTO.setCategory("Wakaf");
                            transactionDTO.setTransactionName(((Wakaf) categoryData).getCategoryName());
                        }
                    }

                    donaturTransactionsHistory.add(transactionDTO);
                }
            }

            return donaturTransactionsHistory;
        }

        throw new BadRequestException("Donatur tidak ditemukan");
    }


    @Override
    public Map<String, Double> getTransactionSummaryForDonatur(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Donatur donatur = donaturRepository.findByEmail(userDetails.getEmail())
                    .or(() -> donaturRepository.findByPhoneNumber(userDetails.getPhoneNumber())) // Coba cari berdasarkan email jika phoneNumber tidak ditemukan
                    .orElseThrow(() -> new BadRequestException("DONATUR TIDAK ADA"));

            Map<String, Double> summary = new HashMap<>();

            // Infak
            Double infakTotal = transactionRepository.sumTransactionByDonaturAndCategory(donatur.getEmail(), donatur.getPhoneNumber(), "Infak");
            summary.put("Infak", infakTotal != null ? infakTotal : 0.0);

            // Wakaf
            Double wakafTotal = transactionRepository.sumTransactionByDonaturAndCategory(donatur.getEmail(), donatur.getPhoneNumber(), "Wakaf");
            summary.put("Wakaf", wakafTotal != null ? wakafTotal : 0.0);

            // Zakat
            Double zakatTotal = transactionRepository.sumTransactionByDonaturAndCategory(donatur.getEmail(), donatur.getPhoneNumber(), "Zakat");
            summary.put("Zakat", zakatTotal != null ? zakatTotal : 0.0);

            // Campaign
            Double campaignTotal = transactionRepository.sumTransactionByDonaturAndCategory(donatur.getEmail(), donatur.getPhoneNumber(), "Campaign");
            summary.put("Campaign", campaignTotal != null ? campaignTotal : 0.0);

            Double dsklTotal = transactionRepository.sumTransactionByDonaturAndCategory(donatur.getEmail(), donatur.getPhoneNumber(), "dskl");
            summary.put("Dskl", dsklTotal != null ? dsklTotal : 0.0);

            // Total keseluruhan
            double total = summary.values().stream().mapToDouble(Double::doubleValue).sum();
            summary.put("Total", total);

            return summary;
        }

        throw new BadRequestException("Authentication error.");
    }


    @Override
    public Page<Transaction> searchTransactions(String search, Pageable pageable) {
        return transactionRepository.searchTransactions(search, pageable);
    }


    private Object getCategoryData(Transaction transaction) {
        // Mengambil data berdasarkan kategori
        switch (transaction.getCategory()) {
            case "zakat":
                return transaction.getZakat();
            case "infak":
                return transaction.getInfak();
            case "wakaf":
                return transaction.getWakaf();
            case "dskl":
                return transaction.getDskl();
            case "campaign":
                return transaction.getCampaign();
            default:
                return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllDonatur() {
        List<Object[]> results = transactionRepository.findAllDonaturMinimal();

        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", row[0]);
            map.put("transactionDate", row[1]);
            map.put("amount", ((Double) row[2]) % 1 == 0 ? ((Double) row[2]).longValue() : row[2]);
            return map;
        }).collect(Collectors.toList());
    }
    @Override
    public ResponseMessage updateJurnalUmumPenyaluran(String nomorBukti, JurnalUmumRequest jurnalUmumRequest) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            // Cari semua transaksi dengan nomor bukti
            List<Transaction> transaksiList = transactionRepository.findByNomorBukti(nomorBukti);
            if (transaksiList.isEmpty()) {
                throw new BadRequestException("Transaksi dengan nomor bukti " + nomorBukti + " tidak ditemukan");
            }

            // ✅ Pastikan request valid dulu
            if (jurnalUmumRequest.getTransactionDate() == null ||
                    jurnalUmumRequest.getDescription() == null ||
                    jurnalUmumRequest.getCategoryType() == null ||
                    jurnalUmumRequest.getDebitDetails() == null ||
                    jurnalUmumRequest.getKreditDetails() == null) {
                throw new BadRequestException("Field transaksi wajib diisi lengkap");
            }

            // Hitung nominal debit & kredit
            Double nominalDebet = Optional.ofNullable(jurnalUmumRequest.getDebitDetails())
                    .orElse(List.of())
                    .stream()
                    .mapToDouble(JurnalUmumRequest.DebitDetail::getAmount)
                    .sum();

            Double nominalKredit = Optional.ofNullable(jurnalUmumRequest.getKreditDetails())
                    .orElse(List.of())
                    .stream()
                    .mapToDouble(JurnalUmumRequest.KreditDetail::getAmount)
                    .sum();

            if (nominalDebet <= 0 || nominalKredit <= 0) {
                throw new BadRequestException("Nominal debit dan kredit wajib lebih dari 0");
            }

            // Validasi COA debit
            Long coaDebitId = jurnalUmumRequest.getDebitDetails().get(0).getCoaId();
            Coa coaDebit = coaRepository.findById(coaDebitId)
                    .orElseThrow(() -> new BadRequestException("COA Debet tidak ditemukan"));

            // Validasi COA kredit
            for (JurnalUmumRequest.KreditDetail kreditDetail : jurnalUmumRequest.getKreditDetails()) {
                coaRepository.findById(kreditDetail.getCoaId())
                        .orElseThrow(() -> new BadRequestException("COA Kredit tidak ditemukan: " + kreditDetail.getCoaId()));
            }

            // ✅ Semua valid → baru hapus transaksi lama
            transactionRepository.deleteAll(transaksiList);

            // Flag penyaluran
            boolean isPenyaluran = jurnalUmumRequest.isPenyaluran();

            // Buat transaksi debit
            Transaction transactionDebit = new Transaction();
            transactionDebit.setTransactionDate(jurnalUmumRequest.getTransactionDate().atStartOfDay());
            transactionDebit.setUsername("Teller Manual");
            transactionDebit.setMessage(jurnalUmumRequest.getDescription());
            transactionDebit.setDebit(nominalDebet);
            transactionDebit.setKredit(0.0);
            transactionDebit.setCoa(coaDebit);
            transactionDebit.setNomorBukti(nomorBukti); // tetap pakai nomor bukti lama
            transactionDebit.setTransactionAmount(nominalDebet);
            transactionDebit.setMethod("OFFLINE");
            transactionDebit.setChannel("Teller");
            transactionDebit.setPenyaluran(isPenyaluran);
            transactionDebit.setSuccess(true);
            transactionDebit.setCategory(jurnalUmumRequest.getCategoryType());

            switch (jurnalUmumRequest.getCategoryType().toLowerCase()) {
                case "campaign":
                    Campaign campaign = campaignRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
                    transactionDebit.setCampaign(campaign);
                    break;
                case "zakat":
                    Zakat zakat = zakatRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
                    transactionDebit.setZakat(zakat);
                    break;
                case "infak":
                    Infak infak = infakRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
                    transactionDebit.setInfak(infak);
                    break;
                case "dskl":
                    DSKL dskl = dsklRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
                    transactionDebit.setDskl(dskl);
                    break;
                case "wakaf":
                    Wakaf wakaf = wakafRepository.findById(jurnalUmumRequest.getCategoryId())
                            .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
                    transactionDebit.setWakaf(wakaf);
                    break;
                case "pengelola":
                    break;
                default:
                    throw new BadRequestException("Invalid category type: " + jurnalUmumRequest.getCategoryType().toLowerCase()); // Exception handling update
            }

            transactionRepository.save(transactionDebit);

            // Buat transaksi kredit
            for (JurnalUmumRequest.KreditDetail kreditDetail : jurnalUmumRequest.getKreditDetails()) {
                Transaction transactionKredit = new Transaction();
                transactionKredit.setTransactionDate(jurnalUmumRequest.getTransactionDate().atStartOfDay());
                transactionKredit.setUsername("Teller Manual");
                transactionKredit.setMessage(jurnalUmumRequest.getDescription());
                transactionKredit.setDebit(0.0);
                transactionKredit.setKredit(kreditDetail.getAmount());
                transactionKredit.setCoa(coaRepository.findById(kreditDetail.getCoaId())
                        .orElseThrow(() -> new BadRequestException("COA Kredit tidak ditemukan")));
                transactionKredit.setNomorBukti(nomorBukti);
                transactionKredit.setTransactionAmount(nominalDebet);
                transactionKredit.setMethod("OFFLINE");
                transactionKredit.setChannel("Teller");
                transactionKredit.setPenyaluran(isPenyaluran);
                transactionKredit.setSuccess(true);
                transactionKredit.setCategory(jurnalUmumRequest.getCategoryType());
                switch (jurnalUmumRequest.getCategoryType().toLowerCase()) {
                    case "campaign":
                        Campaign campaign = campaignRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
                        transactionKredit.setCampaign(campaign);
                        break;
                    case "zakat":
                        Zakat zakat = zakatRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
                        transactionKredit.setZakat(zakat);
                        break;
                    case "infak":
                        Infak infak = infakRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
                        transactionKredit.setInfak(infak);
                        break;
                    case "dskl":
                        DSKL dskl = dsklRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
                        transactionKredit.setDskl(dskl);
                        break;
                    case "wakaf":
                        Wakaf wakaf = wakafRepository.findById(jurnalUmumRequest.getCategoryId())
                                .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
                        transactionKredit.setWakaf(wakaf);
                        break;
                    case "pengelola":
                        break;
                    default:
                        throw new BadRequestException("Invalid category type: " + jurnalUmumRequest.getCategoryType().toLowerCase()); // Exception handling update
                }
                transactionRepository.save(transactionKredit);
            }

            return new ResponseMessage(true, "Jurnal umum dengan nomor bukti " + nomorBukti + " berhasil diperbarui");
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }


    @Override
    public Map<String, Object> getJurnalUmumByNomorBukti(String nomorBukti) throws BadRequestException {
        List<Transaction> transaksiList = transactionRepository.findByNomorBukti(nomorBukti);
        if (transaksiList.isEmpty()) {
            throw new BadRequestException("Transaksi dengan nomor bukti " + nomorBukti + " tidak ditemukan");
        }

        // Ambil transaksi debit (anggap cuma 1 baris debit utama)
        Transaction transaksiDebit = transaksiList.stream()
                .filter(t -> t.getDebit() > 0)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Transaksi debit tidak ditemukan"));

        // Mapping ke request
        JurnalUmumRequest response = new JurnalUmumRequest();
        response.setTransactionDate(transaksiDebit.getTransactionDate().toLocalDate());
        response.setDescription(transaksiDebit.getMessage());
        response.setCategoryType(transaksiDebit.getCategory());
        response.setCategoryId(
                transaksiDebit.getCampaign() != null ? transaksiDebit.getCampaign().getCampaignId() :
                        transaksiDebit.getZakat()   != null ? transaksiDebit.getZakat().getId() :
                                transaksiDebit.getInfak()   != null ? transaksiDebit.getInfak().getId() :
                                        transaksiDebit.getWakaf()   != null ? transaksiDebit.getWakaf().getId() :
                                                transaksiDebit.getDskl()    != null ? transaksiDebit.getDskl().getId() : null
        );
        response.setPenyaluran(transaksiDebit.isPenyaluran());

        // Debit detail
        JurnalUmumRequest.DebitDetail debitDetail = new JurnalUmumRequest.DebitDetail();
        debitDetail.setCoaId(transaksiDebit.getCoa().getId());
        debitDetail.setAmount(transaksiDebit.getDebit());
        response.setDebitDetails(List.of(debitDetail));

        // Kredit details
        List<JurnalUmumRequest.KreditDetail> kreditDetails = transaksiList.stream()
                .filter(t -> t.getKredit() > 0)
                .map(t -> {
                    JurnalUmumRequest.KreditDetail kd = new JurnalUmumRequest.KreditDetail();
                    kd.setCoaId(t.getCoa().getId());
                    kd.setAmount(t.getKredit());
                    return kd;
                })
                .toList();
        response.setKreditDetails(kreditDetails);

        // Tambah categoryName biar sama kaya getAllPenyaluran
        String categoryName =
                transaksiDebit.getCampaign() != null ? transaksiDebit.getCampaign().getCampaignName() :
                        transaksiDebit.getZakat()   != null ? transaksiDebit.getZakat().getCategoryName() :
                                transaksiDebit.getInfak()   != null ? transaksiDebit.getInfak().getCategoryName() :
                                        transaksiDebit.getWakaf()   != null ? transaksiDebit.getWakaf().getCategoryName() :
                                                transaksiDebit.getDskl()    != null ? transaksiDebit.getDskl().getCategoryName() : null;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("nomorBukti", nomorBukti);
        result.put("categoryName", categoryName);
        result.put("jurnal", response);

        return result;
    }


    @Override
    public List<Map<String, Object>> getAllPenyaluran() {
        List<Transaction> transaksiList = transactionRepository.findByPenyaluranTrueOrderByNomorBuktiDesc();

        // Group by nomorBukti → 1 nomor bukti = 1 JurnalUmumRequest
        Map<String, List<Transaction>> grouped = transaksiList.stream()
                .collect(Collectors.groupingBy(Transaction::getNomorBukti,
                        LinkedHashMap::new, // supaya urut sesuai query
                        Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : grouped.entrySet()) {
            String nomorBukti = entry.getKey();
            List<Transaction> group = entry.getValue();

            Transaction debitTx = group.stream()
                    .filter(t -> t.getDebit() > 0)
                    .findFirst()
                    .orElse(null);

            if (debitTx == null) continue;

            JurnalUmumRequest response = new JurnalUmumRequest();
            response.setTransactionDate(debitTx.getTransactionDate().toLocalDate());
            response.setDescription(debitTx.getMessage());
            response.setCategoryType(debitTx.getCategory());
            response.setCategoryId(
                    debitTx.getCampaign() != null ? debitTx.getCampaign().getCampaignId() :
                            debitTx.getZakat()   != null ? debitTx.getZakat().getId() :
                                    debitTx.getInfak()   != null ? debitTx.getInfak().getId() :
                                            debitTx.getWakaf()   != null ? debitTx.getWakaf().getId() :
                                                    debitTx.getDskl()    != null ? debitTx.getDskl().getId() : null
            );
            response.setPenyaluran(true);

            // debit
            JurnalUmumRequest.DebitDetail debitDetail = new JurnalUmumRequest.DebitDetail();
            debitDetail.setCoaId(debitTx.getCoa().getId());
            debitDetail.setAmount(debitTx.getDebit());
            response.setDebitDetails(List.of(debitDetail));

            // kredit
            List<JurnalUmumRequest.KreditDetail> kreditDetails = group.stream()
                    .filter(t -> t.getKredit() > 0)
                    .map(t -> {
                        JurnalUmumRequest.KreditDetail kd = new JurnalUmumRequest.KreditDetail();
                        kd.setCoaId(t.getCoa().getId());
                        kd.setAmount(t.getKredit());
                        return kd;
                    })
                    .toList();
            response.setKreditDetails(kreditDetails);

            // category name
            String categoryName =
                    debitTx.getCampaign() != null ? debitTx.getCampaign().getCampaignName() :
                            debitTx.getZakat()   != null ? debitTx.getZakat().getCategoryName() :
                                    debitTx.getInfak()   != null ? debitTx.getInfak().getCategoryName() :
                                            debitTx.getWakaf()   != null ? debitTx.getWakaf().getCategoryName() :
                                                    debitTx.getDskl()    != null ? debitTx.getDskl().getCategoryName() : null;

            // Bungkus response
            Map<String, Object> mapResponse = new LinkedHashMap<>();
            mapResponse.put("nomorBukti", nomorBukti);
            mapResponse.put("categoryName", categoryName);
            mapResponse.put("jurnal", response);


            result.add(mapResponse);
        }

        return result;
    }


}