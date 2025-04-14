package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.CampaignResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Distribution;
import com.lazis.lazissultanagung.model.NomZakat;
import com.lazis.lazissultanagung.service.CampaignService;
import com.lazis.lazissultanagung.service.DistributionService;
import com.lazis.lazissultanagung.service.NomZakatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/nomZakat")
public class NomZakatController {

    @Autowired
    private NomZakatService nomzakatService;


    // Add a new NomZakat
    @PostMapping("/add")
    public ResponseEntity<NomZakat> addPercentage(@RequestBody NomZakat nomZakat) {
        NomZakat savedPercentage = nomzakatService.addNomZakat(nomZakat);
        return ResponseEntity.ok(savedPercentage);
    }

    // Edit an existing NomZakat by ID
    @PutMapping("/edit/{id}")
    public ResponseEntity<NomZakat> editPercentage(
            @PathVariable Long id,
            @RequestBody NomZakat nomZakat) {
        try {
            NomZakat updatedPercentage = nomzakatService.editNomZakat(id, nomZakat);
            return ResponseEntity.ok(updatedPercentage);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get all NomZakat
    @GetMapping
    public ResponseEntity<List<NomZakat>> getAllPercentage() {
        List<NomZakat> percentages = nomzakatService.getAllNomZakat();
        return ResponseEntity.ok(percentages);
    }

    // Get a NomZakat by ID
    @GetMapping("/{id}")
    public ResponseEntity<NomZakat> getPercentageById(@PathVariable Long id) {
        return nomzakatService.getNomZakatById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get the NomZakat with ID = 1
    @GetMapping("/get/one")
    public ResponseEntity<NomZakat> getPercentageByIdOne() {
        return nomzakatService.getNomZakatByIdOne()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
