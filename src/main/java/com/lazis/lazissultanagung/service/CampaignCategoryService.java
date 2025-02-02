package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.CampaignCategory;

import java.util.List;

public interface CampaignCategoryService {
    List<CampaignCategory> getAllCampaignCategory();

    CampaignCategory createCampaignCategory(CampaignCategory campaignCategory);

    CampaignCategory updateCampaignCategory(Long id, CampaignCategory campaignCategory);

    ResponseMessage deleteCampaignCategory(Long id);
}
