package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.DashboardImageRequest;
import com.lazis.lazissultanagung.model.DashboardImage;
import com.lazis.lazissultanagung.service.DashboardImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/dashboardImage")
public class DashboardImageController {

    @Autowired
    private DashboardImageService dashboardImageService;

    @GetMapping
    public ResponseEntity<List<DashboardImage>> getAllImage() {
        List<DashboardImage> dashboardImageList = dashboardImageService.getAllDashboardImage();
        return ResponseEntity.ok(dashboardImageList);
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

}
