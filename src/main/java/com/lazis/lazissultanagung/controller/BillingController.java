package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.TransactionRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Billing;
import com.lazis.lazissultanagung.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping("/billing/{categoryType}/{id}")
    public ResponseEntity<?> createTransaction(@PathVariable String categoryType,
                                               @PathVariable Long id,
                                               @RequestBody TransactionRequest transactionRequest) {
        try {
            Billing billing = billingService.createBilling(categoryType, id, transactionRequest);
            return ResponseEntity.ok(billing);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
