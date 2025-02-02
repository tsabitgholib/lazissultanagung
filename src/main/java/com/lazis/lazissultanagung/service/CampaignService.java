package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.CampaignRequest;
import com.lazis.lazissultanagung.dto.response.CampaignResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Campaign;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CampaignService {
    CampaignResponse createCampaign(CampaignRequest campaignRequest);

    CampaignResponse editCampaign(Long id, CampaignRequest campaignRequest);

    List<CampaignResponse> getAllCampaign();


    Optional<CampaignResponse> getCampaignById(Long id);

    ResponseMessage deleteCampaign(Long id);

    ResponseMessage closeCampaign(Long id);

    @Transactional
    ResponseMessage approveCampaign(Long id) throws BadRequestException;

    Page<CampaignResponse> getCampaignByActiveAndApproved(Pageable pageable);

    Page<CampaignResponse> getCampaignsByCategoryName(String categoryName, Pageable pageable);

    Page<CampaignResponse> getCampaignByName(String campaignName, Pageable pageable);

    Page<CampaignResponse> getCampaignByNamePending(String campaignName, Pageable pageable);

    Page<CampaignResponse> getCampaignByNameNonaktif(String campaignName, Pageable pageable);

    Page<CampaignResponse> getCampaignByEmergency(Pageable pageable);

    Page<CampaignResponse> getPendingCampaign(Pageable pageable);

    Page<CampaignResponse> getHistoryCampaign(Pageable pageable);

    Page<CampaignResponse> getCampaignsByOperator(Pageable pageable);

    Page<CampaignResponse> getActiveApproveCampaignsByOperator(Pageable pageable);

    Page<CampaignResponse> getPendingCampaignsByOperator(Pageable pageable);

    Page<CampaignResponse> getHistoryCampaignsByOperator(Pageable pageable);
}
