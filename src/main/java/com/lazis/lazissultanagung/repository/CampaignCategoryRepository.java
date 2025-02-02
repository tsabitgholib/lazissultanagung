package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.CampaignCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {

}
