package com.lazis.lazissultanagung.dto.response;

import com.lazis.lazissultanagung.model.Transaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long displayid;
    private String nomorBukti;
    private String username;
    private String phoneNumber;
    private String email;
    private Double transactionAmount;
    private String message;
    private LocalDateTime transactionDate;
    private String category;
    private String method;
    private String channel;
    private boolean success;
    private Object categoryData;

    public TransactionResponse(Transaction transaction, Object categoryData) {
        this.id = transaction.getTransactionId();
        this.nomorBukti = transaction.getNomorBukti();
        this.username = transaction.getUsername();
        this.phoneNumber = transaction.getPhoneNumber();
        this.email = transaction.getEmail();
        this.transactionAmount = transaction.getTransactionAmount();
        this.message = transaction.getMessage();
        this.transactionDate = transaction.getTransactionDate();
        this.category = transaction.getCategory();
        this.method = transaction.getMethod();
        this.channel = transaction.getChannel();
        this.success = transaction.isSuccess();
        this.categoryData = categoryData;

        if ("Teller Manual".equalsIgnoreCase(transaction.getUsername())) {
            this.username = transaction.getMessage();
            this.message = transaction.getUsername();
        } else {
            this.username = transaction.getUsername();
            this.message = transaction.getMessage();
        }
    }

}

