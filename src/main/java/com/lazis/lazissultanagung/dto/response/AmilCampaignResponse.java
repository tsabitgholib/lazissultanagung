package com.lazis.lazissultanagung.dto.response;

import lombok.Data;


@Data
public class AmilCampaignResponse {
    private long campaignId;
    private String campaignName;
    private String location;
    private double targetAmount;
    private double currentAmount;
    private double amil;
    private boolean active;

    public AmilCampaignResponse(long campaignId, String campaignName, String location, double targetAmount, double currentAmount, double amil, boolean active) {
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.location = location;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.amil = amil;
        this.active = active;
    }

}
