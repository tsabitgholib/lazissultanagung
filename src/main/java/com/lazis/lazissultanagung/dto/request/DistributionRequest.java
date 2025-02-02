package com.lazis.lazissultanagung.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class DistributionRequest {
    private double distributionAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate distributionDate;

    private String receiver;

    private MultipartFile image;

    private String description;
}