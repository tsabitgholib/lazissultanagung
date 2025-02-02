package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.PercentageForCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PercentageForCampaignRepository extends JpaRepository<PercentageForCampaign, Long> {
}
