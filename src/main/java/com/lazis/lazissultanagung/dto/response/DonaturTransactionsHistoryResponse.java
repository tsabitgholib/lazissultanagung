package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DonaturTransactionsHistoryResponse {
    private String username;
    private String category;
    private String transactionName;
    private double transactionAmount;
    private String message;
    private LocalDateTime transactionDate;
    private boolean success;
}
