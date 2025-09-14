package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.DistributionRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Distribution;
import com.lazis.lazissultanagung.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/distribution")
public class DistributionController {

    @Autowired
    private DistributionService distributionService;

    @GetMapping
    public ResponseEntity<Page<Distribution>> getAllDistributions(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Pageable pageable) {
        Page<Distribution> distributions = distributionService.getAllDistributions(month, year, pageable);
        return new ResponseEntity<>(distributions, HttpStatus.OK);
    }

    @GetMapping("/{category}/{id}")
    public List<Distribution> getDistributionsByCategoryAndId(@PathVariable String category, @PathVariable Long id) {
        return distributionService.getDistributionsByCategoryAndId(category, id);
    }

    // Create a new distribution
    @PostMapping("/{categoryType}/{id}")
    public ResponseEntity<?> createDistribution(
            @PathVariable String categoryType,
            @PathVariable Long id,
            @ModelAttribute DistributionRequest distributionRequest) {
        try {
            Distribution distribution = distributionService.createDistribution(categoryType, id, distributionRequest);
            return new ResponseEntity<>(distribution, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/edit/{distributionId}")
    public ResponseEntity<?> updateDistribution(
            @PathVariable Long distributionId,
            @ModelAttribute DistributionRequest distributionRequest) {
        try {
            Distribution distribution = distributionService.updateDistribution(distributionId, distributionRequest);
            return ResponseEntity.ok(distribution);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{distributionId}")
    public ResponseEntity<?> getDistributionById(@PathVariable Long distributionId) {
        try {
            Distribution distribution = distributionService.getDistributionById(distributionId);
            return ResponseEntity.ok(distribution);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

