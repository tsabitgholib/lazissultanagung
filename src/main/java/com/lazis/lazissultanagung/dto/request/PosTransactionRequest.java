package com.lazis.lazissultanagung.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class PosTransactionRequest {
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String channel;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private String description;
    private String categoryType;
    private Long categoryId;
    private Double amount;
    private String paymentMethod;
    private Long eventId;
    
    private MultipartFile image;
}
