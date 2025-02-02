package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.CampaignCategory;
import com.lazis.lazissultanagung.service.CampaignCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/campaignCategory")
public class CampaignCategoryController {

    @Autowired
    private CampaignCategoryService campaignCategoryService;

    @GetMapping
    public ResponseEntity<List<CampaignCategory>> getAllCampaignCategory(){
        List<CampaignCategory> campaignCategories = campaignCategoryService.getAllCampaignCategory();
        return ResponseEntity.ok().body(campaignCategories);
    }

    @PostMapping("/create")
    public ResponseEntity<CampaignCategory> createCampaignCategory(@RequestBody CampaignCategory campaignCategory){
        CampaignCategory createCategory = campaignCategoryService.createCampaignCategory(campaignCategory);
        return ResponseEntity.ok().body(createCategory);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CampaignCategory> updateCampaign(@PathVariable Long id, @RequestBody CampaignCategory campaignCategory) {
        CampaignCategory updateCategory = campaignCategoryService.updateCampaignCategory(id, campaignCategory);
        return ResponseEntity.ok().body(updateCategory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteCampaignCategory(@PathVariable Long id) {
        ResponseMessage response = campaignCategoryService.deleteCampaignCategory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
