package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.EditProfileDonaturRequest;
import com.lazis.lazissultanagung.model.Donatur;
import com.lazis.lazissultanagung.service.DonaturService;
import com.lazis.lazissultanagung.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/donatur")
public class DonaturController {

    @Autowired
    private DonaturService donaturService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser(){
        Donatur currentDonatur = donaturService.getCurrentDonatur();
        return ResponseEntity.ok(currentDonatur);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editUserProfile(@ModelAttribute EditProfileDonaturRequest editProfileRequest) {
        Donatur updateDonatur = donaturService.editProfileDonatur(editProfileRequest);
        return ResponseEntity.ok(updateDonatur);
    }

    @GetMapping()
    public Page<Donatur> getAllDonatur(@RequestParam(name = "page", defaultValue = "0") int page){
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return donaturService.getAllDonatur(pageRequest);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Donatur>> searchDonaturs(
            @RequestParam String search,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        Page<Donatur> result = donaturService.searchDonaturs(search,pageRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/summary")
    public Map<String, Double> getTransactionSummary(Authentication authentication) {
        // Memanggil service untuk mendapatkan summary transaksi
        return transactionService.getTransactionSummaryForDonatur(authentication);
    }
}
