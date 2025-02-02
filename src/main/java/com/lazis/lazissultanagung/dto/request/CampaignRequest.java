package com.lazis.lazissultanagung.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class CampaignRequest {

    private long campaignId;
    private long categoryId;
    private String campaignName;
    private String campaignCode;
    private MultipartFile campaignImage;
    private String description;
    private String location;
    private double targetAmount;
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private boolean emergency;
}
