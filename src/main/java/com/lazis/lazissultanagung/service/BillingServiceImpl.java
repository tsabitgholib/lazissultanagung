package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.TransactionRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.*;
import com.lazis.lazissultanagung.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

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
    private PasswordEncoder encoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Billing createBilling(String categoryType, Long id, TransactionRequest transactionRequest) throws BadRequestException {
        // Validasi username dan phoneNumber
        if (transactionRequest.getUsername() == null || transactionRequest.getPhoneNumber() == null) {
            throw new BadRequestException("Username and phoneNumber cannot be null for billing");
        }

        // Buat objek tagihan berdasarkan data dari transactionRequest
        Billing billing = modelMapper.map(transactionRequest, Billing.class);
        billing.setUsername(transactionRequest.getUsername());
        billing.setPhoneNumber(transactionRequest.getPhoneNumber());

        // Generate vaNumber secara dinamis
        Random random = new Random();
        long min = 1000000000L;
        long max = 9999999999L;
        long vaNumber = min + (long) (random.nextDouble() * (max - min));
        String vaNumberStr = "02029" + vaNumber;
        billing.setPrefix("02029");
        billing.setVaNumber(vaNumberStr);
        billing.setTime("12345");

        // Tentukan kategori dan set entitas terkait ke tagihan
        switch (categoryType) {
            case "campaign":
                Campaign campaign = campaignRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
                billing.setCampaign(campaign);
                billing.setAccount("00002");
                break;
            case "zakat":
                Zakat zakat = zakatRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
                billing.setZakat(zakat);
                billing.setAccount("00001");
                break;
            case "infak":
                Infak infak = infakRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
                billing.setInfak(infak);
                billing.setAccount("00002");
                break;
            case "wakaf":
                Wakaf wakaf = wakafRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
                billing.setWakaf(wakaf);
                billing.setAccount("00003");
                break;
            case "dskl":
                DSKL dskl = dsklRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
                billing.setDskl(dskl);
                billing.setAccount("00003");
                break;
            default:
                throw new IllegalArgumentException("Invalid billing type: " + categoryType);
        }

        // Set sisa data tagihan
        billing.setBillingAmount(transactionRequest.getTransactionAmount());
        billing.setBillingDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        billing.setCategory(categoryType);
        billing.setMethod(transactionRequest.getMethod());
        billing.setSuccess(false);

        // Mendapatkan donatur yang login (jika ada)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Donatur donatur = donaturRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Donatur tidak ditemukan"));
            billing.setDonatur(donatur); // Set donatur yang login
        } else {
            billing.setDonatur(null); // Tidak ada donatur yang login, set null
        }

        // Simpan tagihan ke dalam database
        return billingRepository.save(billing);
    }




//    @Override
//    public boolean getBillingSuccess(Long billingId) {
//        Optional<Boolean> success = billingRepository.findSuccessByBillingId(billingId);
//        return success.orElse(false);
//    }
}



