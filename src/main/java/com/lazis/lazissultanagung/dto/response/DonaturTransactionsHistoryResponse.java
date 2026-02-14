package com.lazis.lazissultanagung.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DonaturTransactionsHistoryResponse {
    private String username;
    private String category;
    private String transactionName;
    private double transactionAmount;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private LocalDateTime transactionDate;
    private boolean success;
}
