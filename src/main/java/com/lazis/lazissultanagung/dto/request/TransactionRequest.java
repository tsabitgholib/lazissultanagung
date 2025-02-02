package com.lazis.lazissultanagung.dto.request;

import lombok.Data;

@Data
public class TransactionRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private double transactionAmount;
    private String message;
}