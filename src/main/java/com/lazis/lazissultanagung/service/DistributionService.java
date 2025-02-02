package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.DistributionRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Distribution;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DistributionService {
    Page<Distribution> getAllDistributions(Integer month, Integer year, Pageable pageable);

    List<Distribution> getDistributionsByCategoryAndId(String category, Long id);

    Distribution createDistribution(String categoryType, Long id, DistributionRequest distributionRequest) throws BadRequestException;
}
