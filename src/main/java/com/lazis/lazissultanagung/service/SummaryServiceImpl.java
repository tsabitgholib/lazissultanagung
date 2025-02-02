package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.AmilCampaignResponse;
import com.lazis.lazissultanagung.dto.response.AmilZiswafResponse;
import com.lazis.lazissultanagung.dto.response.SummaryResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryServiceImpl implements SummaryService {

    @Autowired
    private DistributionRepository distributionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DonaturRepository donaturRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ZakatRepository zakatRepository;

    @Autowired
    private InfakRepository infakRepository;

    @Autowired
    private WakafRepository wakafRepository;

    @Autowired
    private DSKLRepository dsklRepository;

    @Override
    public SummaryResponse getSummary() {
        SummaryResponse summary = new SummaryResponse();
            summary.setTotalDistributionAmount(distributionRepository.totalDistributionAmount());
            summary.setTotalDistributionReceiver(distributionRepository.totalDistributionReceiver());
            summary.setTotalTransactionAmount(transactionRepository.totalTransactionAmount());
            summary.setTotalDonatur(transactionRepository.getTotalDonatur());
        return summary;
    }

    @Override
    public SummaryResponse getSummaryOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Operator tidak terautentikasi");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

        SummaryResponse summary = new SummaryResponse();
        summary.setTotalDistributionAmount(
                distributionRepository.totalDistributionAmountByOperator(existingAdmin.getId())
        );
        summary.setTotalDistributionReceiver(
                distributionRepository.totalDistributionReceiverByOperator(existingAdmin.getId())
        );
        summary.setTotalTransactionAmount(
                transactionRepository.totalTransactionAmountByOperator(existingAdmin.getId())
        );
        summary.setTotalDonatur(
                transactionRepository.getTotalDonaturByOperator(existingAdmin.getId())
        );

        return summary;
    }

    @Override
    public Page<Object> getAmilByCategory(String category, Pageable pageable) {
        List<Object> response = new ArrayList<>();
        Page<Object> resultPage;

        // Gunakan array untuk menyimpan total
        double[] totals = {0.0, 0.0}; // index 0: totalAmount, index 1: totalAmil

        switch (category.toLowerCase()) {
            case "zakat":
                resultPage = zakatRepository.findAll(pageable).map(zakat -> {
                    double amount = zakat.getAmount();
                    double amil = amount * 0.125;
                    totals[0] += amount; // Menambahkan ke totalAmount
                    totals[1] += amil;   // Menambahkan ke totalAmil
                    // Tambahkan objek AmilZiswafResponse ke dalam list response
                    return new AmilZiswafResponse(
                            zakat.getId(),
                            "zakat",
                            zakat.getCategoryName(),
                            amount,
                            amil
                    );
                });
                break;

            case "infak":
                resultPage = infakRepository.findAll(pageable).map(infak -> {
                    double amount = infak.getAmount();
                    double amil = amount * 0.125;
                    totals[0] += amount;
                    totals[1] += amil;
                    return new AmilZiswafResponse(
                            infak.getId(),
                            "infak",
                            infak.getCategoryName(),
                            amount,
                            amil
                    );
                });
                break;

            case "wakaf":
                resultPage = wakafRepository.findAll(pageable).map(wakaf -> {
                    double amount = wakaf.getAmount();
                    double amil = amount * 0.10;
                    totals[0] += amount;
                    totals[1] += amil;
                    return new AmilZiswafResponse(
                            wakaf.getId(),
                            "wakaf",
                            wakaf.getCategoryName(),
                            amount,
                            amil
                    );
                });
                break;

            case "dskl":
                resultPage = dsklRepository.findAll(pageable).map(dskl -> {
                    double amount = dskl.getAmount();
                    double amil = amount * 0.125;
                    totals[0] += amount;
                    totals[1] += amil;
                    return new AmilZiswafResponse(
                            dskl.getId(),
                            "dskl",
                            dskl.getCategoryName(),
                            amount,
                            amil
                    );
                });
                break;

            case "campaign":
                resultPage = campaignRepository.findAll(pageable).map(campaign -> {
                    double amount = campaign.getCurrentAmount();
                    double amil = amount * 0.125;
                    totals[0] += amount;
                    totals[1] += amil;
                    return new AmilCampaignResponse(
                            campaign.getCampaignId(),
                            campaign.getCampaignName(),
                            campaign.getLocation(),
                            campaign.getTargetAmount(),
                            amount,
                            amil,
                            campaign.isActive()
                    );
                });
                break;

            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }

        Map<String, Double> totalResponse = new HashMap<>();
        totalResponse.put("totalAmount", totals[0]);
        totalResponse.put("totalAmil", totals[1]);
        response.add(totalResponse);

        List<Object> content = new ArrayList<>(resultPage.getContent());
        content.addAll(response);

        return new PageImpl<>(content, resultPage.getPageable(), resultPage.getTotalElements());
    }

    @Override
    public Map<String, Object> getCampaignSummary() {
        double totalTargetAmount = campaignRepository.getTotalTargetAmount();
        double totalCurrentAmount = campaignRepository.getTotalCurrentAmount();

        // Membuat Map untuk respons
        Map<String, Object> response = new HashMap<>();
        response.put("totalTargetAmount", totalTargetAmount);
        response.put("totalCurrentAmount", totalCurrentAmount);

        return response;
    }

    @Override
    public Map<String, Double> getTotalIncomeSummary() {
        Map<String, Double> summary = new HashMap<>();
        summary.put("campaign", campaignRepository.getTotalCurrentAmount());
        summary.put("zakat", zakatRepository.getTotalZakatAmount());
        summary.put("infak", infakRepository.getTotalInfakAmount());
        summary.put("wakaf", wakafRepository.getTotalWakafAmount());
        summary.put("dskl", dsklRepository.getTotalDsklAmount());

        return summary;
    }


//
//    @Override
//    public Optional<SummaryCampaignResponse> getSummaryCampaign() {
//        Optional<Map<String, Double>> summaryMap = campaignRepository.getSummaryCampaign();
//        return summaryMap.map(map -> new SummaryCampaignResponse(
//                map.getOrDefault("totalCampaignTransactionAmount", 0.0),
//                map.getOrDefault("totalAmil", 0.0),
//                map.getOrDefault("totalCampaignDistributionAmount", 0.0)));
//    }

    @Override
    public SummaryResponse getSummaryByCategory(String category) {
        SummaryResponse summary = new SummaryResponse();

        switch (category.toLowerCase()) {
            case "zakat":
                summary.setTotalTransactionAmount(transactionRepository.totalTransactionZakatAmount());
                summary.setTotalDonatur(transactionRepository.getTotalDonaturZakat());
                summary.setTotalDistributionAmount(distributionRepository.totalDistributionZakatAmount());
                summary.setTotalDistributionReceiver(distributionRepository.totalDistributionZakatReceiver());
                break;
            case "infak":
                summary.setTotalTransactionAmount(transactionRepository.totalTransactionInfakAmount());
                summary.setTotalDonatur(transactionRepository.getTotalDonaturInfak());
                summary.setTotalDistributionAmount(distributionRepository.totalDistributionInfakAmount());
                summary.setTotalDistributionReceiver(distributionRepository.totalDistributionInfakReceiver());
                break;
            case "wakaf":
                summary.setTotalTransactionAmount(transactionRepository.totalTransactionWakafAmount());
                summary.setTotalDonatur(transactionRepository.getTotalDonaturWakaf());
                summary.setTotalDistributionAmount(distributionRepository.totalDistributionWakafAmount());
                summary.setTotalDistributionReceiver(distributionRepository.totalDistributionWakafReceiver());
                break;
            case "dskl":
                summary.setTotalTransactionAmount(transactionRepository.totalTransactionDSKLAmount());
                summary.setTotalDonatur(transactionRepository.getTotalDonaturDSKL());
                summary.setTotalDistributionAmount(distributionRepository.totalDistributionDSKLAmount());
                summary.setTotalDistributionReceiver(distributionRepository.totalDistributionDSKLReceiver());
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }

        return summary;
    }
}
