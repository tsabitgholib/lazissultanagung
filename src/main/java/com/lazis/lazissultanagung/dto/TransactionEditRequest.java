package com.lazis.lazissultanagung.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionEditRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private String address;
    private double transactionAmount;
    private String message;
    private LocalDateTime transactionDate;
}
