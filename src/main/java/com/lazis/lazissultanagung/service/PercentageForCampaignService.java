package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.PercentageForCampaign;
import com.lazis.lazissultanagung.repository.PercentageForCampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PercentageForCampaignService {

    @Autowired
    private PercentageForCampaignRepository percentageForCampaignRepository;

    public PercentageForCampaign addPercentage(PercentageForCampaign percentageForCampaign){
        return percentageForCampaignRepository.save(percentageForCampaign);
    }

    public PercentageForCampaign editPercentage(Long id, PercentageForCampaign percentageForCampaign) {
        PercentageForCampaign percentageForCampaign1 = percentageForCampaignRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("gagal mengedit, id tidak ada"));

        percentageForCampaign1.setPercentage(percentageForCampaign.getPercentage());

        return percentageForCampaignRepository.save(percentageForCampaign1);
    }


    public List<PercentageForCampaign> getAllPercentage(){
        return percentageForCampaignRepository.findAll();
    }

    public Optional<PercentageForCampaign> getPercentageById(Long id) {
        return percentageForCampaignRepository.findById(id);
    }

    public Optional<PercentageForCampaign> getPercentageByIdOne() {
        return getPercentageById(1L);
    }
}
