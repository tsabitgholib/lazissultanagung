package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.DashboardImageRequest;
import com.lazis.lazissultanagung.model.DashboardImage;
import com.lazis.lazissultanagung.service.DashboardImageService;
import com.lazis.lazissultanagung.service.DistributionService;
import com.lazis.lazissultanagung.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/dashboardImage")
public class DashboardImageController {

    @Autowired
    private DashboardImageService dashboardImageService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private DistributionService distributionService;

//    @GetMapping
//    public ResponseEntity<List<DashboardImage>> getAllImage() {
//        List<DashboardImage> dashboardImageList = dashboardImageService.getAllDashboardImage();
//        return ResponseEntity.ok(dashboardImageList);
//    }

    @GetMapping
    public List<DashboardImage> getAllDashboardImages() {
        return dashboardImageService.getAllDashboardImage();
    }

    @PostMapping("/create")
    public ResponseEntity<DashboardImage> addDashboardImage(@ModelAttribute DashboardImageRequest dashboardImageRequest) {
        DashboardImage createDashboardImage = dashboardImageService.addDashboardImage(dashboardImageRequest);
        return ResponseEntity.ok(createDashboardImage);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<DashboardImage> editDashboardImage(
            @PathVariable long id,
            @ModelAttribute DashboardImageRequest dashboardImageRequest) {
        DashboardImage editDashboardImage = dashboardImageService.editDashboardImage(id, dashboardImageRequest);
        return ResponseEntity.ok(editDashboardImage);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DashboardImage> deleteImages(
            @PathVariable Long id,
            @RequestParam List<String> images) {
        DashboardImage updatedDashboardImage = dashboardImageService.deleteDashboardImage(id, images);
        return ResponseEntity.ok(updatedDashboardImage);
    }

    @GetMapping("detail-summary")
    public ResponseEntity<?> getDashboardData(@RequestParam String type) {
        switch (type.toLowerCase()) {
            case "penyaluran":
                return ResponseEntity.ok(distributionService.getAllDistributionswkwk());

            case "penerima-manfaat":
                return ResponseEntity.ok(distributionService.getAllPenerimaManfaat());

            case "penghimpunan":
                return ResponseEntity.ok(dashboardImageService.getAllPenghimpunan());

            case "donatur":
                return ResponseEntity.ok(transactionService.getAllDonatur());

            default:
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid type")
                );
        }
    }


}
