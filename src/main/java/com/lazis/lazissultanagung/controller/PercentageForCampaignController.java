package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.CampaignResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Distribution;
import com.lazis.lazissultanagung.model.PercentageForCampaign;
import com.lazis.lazissultanagung.service.CampaignService;
import com.lazis.lazissultanagung.service.DistributionService;
import com.lazis.lazissultanagung.service.PercentageForCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/percentage")
public class PercentageForCampaignController {

    @Autowired
    private PercentageForCampaignService percentageForCampaignService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private CampaignService campaignService;

    // Add a new PercentageForCampaign
    @PostMapping("/add")
    public ResponseEntity<PercentageForCampaign> addPercentage(@RequestBody PercentageForCampaign percentageForCampaign) {
        PercentageForCampaign savedPercentage = percentageForCampaignService.addPercentage(percentageForCampaign);
        return ResponseEntity.ok(savedPercentage);
    }

    // Edit an existing PercentageForCampaign by ID
    @PutMapping("/edit/{id}")
    public ResponseEntity<PercentageForCampaign> editPercentage(
            @PathVariable Long id,
            @RequestBody PercentageForCampaign percentageForCampaign) {
        try {
            PercentageForCampaign updatedPercentage = percentageForCampaignService.editPercentage(id, percentageForCampaign);
            return ResponseEntity.ok(updatedPercentage);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get all PercentageForCampaign
    @GetMapping
    public ResponseEntity<List<PercentageForCampaign>> getAllPercentage() {
        List<PercentageForCampaign> percentages = percentageForCampaignService.getAllPercentage();
        return ResponseEntity.ok(percentages);
    }

    // Get a PercentageForCampaign by ID
    @GetMapping("/{id}")
    public ResponseEntity<PercentageForCampaign> getPercentageById(@PathVariable Long id) {
        return percentageForCampaignService.getPercentageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get the PercentageForCampaign with ID = 1
    @GetMapping("/get/one")
    public ResponseEntity<PercentageForCampaign> getPercentageByIdOne() {
        return percentageForCampaignService.getPercentageByIdOne()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rincian/{campaignId}")
    public ResponseEntity<?> getRincianDana(@PathVariable Long campaignId) {
        // Ambil data campaign
        CampaignResponse campaign = campaignService.getCampaignById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign dengan ID " + campaignId + " tidak ditemukan"));

        // Ambil data distribusi terkait campaign
        List<Distribution> distributions = distributionService.getDistributionsByCategoryAndId("campaign", campaignId);

        // Hitung total distribusi yang sudah disalurkan
        double totalDisbursed = distributions.stream()
                .filter(Distribution::isSuccess)
                .mapToDouble(Distribution::getDistributionAmount)
                .sum();

        // Ambil persentase biaya operasional dari PercentageForCampaign
        PercentageForCampaign percentageForCampaign = percentageForCampaignService.getPercentageByIdOne()
                .orElseThrow(() -> new RuntimeException("Persentase biaya operasional tidak ditemukan"));

        double biayaOperasionalPersen = percentageForCampaign.getPercentage(); // contoh: 4%
        double biayaTransaksiPersen = 6;  // contoh: 6%

        // Hitung total biaya operasional dan transaksi
        double biayaOperasional = campaign.getCurrentAmount() * (biayaOperasionalPersen / 100);

        // Dana untuk penggalangan dana
        double dana_untuk_penggalangan_dana = campaign.getCurrentAmount() - biayaOperasional;

        double biayaTransaksi = dana_untuk_penggalangan_dana * (biayaTransaksiPersen / 100);

        // Hitung dana yang belum disalurkan
        double danaBelumDisalurkan = campaign.getCurrentAmount() - (totalDisbursed + biayaTransaksi);

        // Bangun respons
        Map<String, Object> response = new HashMap<>();
        response.put("campaign_name", campaign.getCampaignName());
        response.put("dana_untuk_penggalangan_dana", dana_untuk_penggalangan_dana);
        response.put("dana_terkumpul", campaign.getCurrentAmount());
        response.put("biaya_transaksi_dan_teknologi", biayaTransaksi);
        response.put("biaya_operasional", biayaOperasional);
        response.put("dana_sudah_disalurkan", totalDisbursed);
        response.put("dana_belum_disalurkan", danaBelumDisalurkan);

        Map<String, Object> detailPersentase = new HashMap<>();
        detailPersentase.put("persen_operasional", biayaOperasionalPersen);
        detailPersentase.put("persen_transaksi", biayaTransaksiPersen);

        response.put("persentase", detailPersentase);

        return ResponseEntity.ok(response);
    }
}
