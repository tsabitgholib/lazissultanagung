package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.AmilCampaignResponse;
import com.lazis.lazissultanagung.dto.response.SummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SummaryService {
    SummaryResponse getSummary();

    SummaryResponse getSummaryOperator();

    Page<Object> getAmilByCategory(String category, Pageable pageable);

    Map<String, Object> getCampaignSummary();

    Map<String, Double> getTotalIncomeSummary();

    SummaryResponse getSummaryByCategory(String category);
}
