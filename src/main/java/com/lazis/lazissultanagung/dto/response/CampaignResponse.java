package com.lazis.lazissultanagung.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CampaignResponse{
    private Long campaignId;
    private int displayId;
    private String campaignCode;
    private String campaignName;
    private String campaignCategory;
    private String campaignImage;
    private String creator;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private double targetAmount;
    private double currentAmount;
    private String location;
    private boolean active;
    private double pengajuan;
    private double realisasi;
    private double distribution;
    private boolean approved;
    private boolean emergency;
}