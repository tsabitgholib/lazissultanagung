package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

@Data
public class SummaryResponse {
    private Double totalDistributionAmount;
    private long totalDistributionReceiver;
    private Double totalTransactionAmount;
    private long totalDonatur;
}
