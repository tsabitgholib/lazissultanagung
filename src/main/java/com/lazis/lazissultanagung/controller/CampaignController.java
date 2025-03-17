package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.CampaignRequest;
import com.lazis.lazissultanagung.dto.response.CampaignResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Campaign;
import com.lazis.lazissultanagung.service.CampaignService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAllCampaign(){
        List<CampaignResponse> campaigns = campaignService.getAllCampaign();
        return ResponseEntity.ok().body(campaigns);
    }

    @PostMapping("/create")
    public ResponseEntity<CampaignResponse> createCampaign(@Valid @ModelAttribute CampaignRequest campaignRequest) {
        try {
            CampaignResponse campaignResponse = campaignService.createCampaign(campaignRequest);
            return new ResponseEntity<>(campaignResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<CampaignResponse> editCampaign(
            @PathVariable("id") Long id,
            @ModelAttribute CampaignRequest campaignRequest) {

        CampaignResponse updatedCampaign = campaignService.editCampaign(id, campaignRequest);
        return ResponseEntity.ok(updatedCampaign);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable Long id){
        Optional<CampaignResponse> campaignOptional = campaignService.getCampaignById(id);
        if (campaignOptional.isPresent()){
            return new ResponseEntity<>(campaignOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteCampaign(@PathVariable Long id){
        return campaignService.deleteCampaign(id);
    }

    @PutMapping("/close/{id}")
    public ResponseMessage closeCampaign(@PathVariable Long id){
        return campaignService.closeCampaign(id);
    }

    @GetMapping("/get-active-and-approved-campaign")
    public Page<CampaignResponse> getCampaignByActiveAndApproved(@RequestParam(name = "page", defaultValue = "0") int page){
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignByActiveAndApproved(pageRequest);
    }

    @CrossOrigin
    @PutMapping("/approve-campaign/{id}")
    public ResponseMessage approveCampaign(@PathVariable Long id) {
        return campaignService.approveCampaign(id);
    }

    @GetMapping("/category")
    public Page<CampaignResponse> getCampaignByCategoryName(@RequestParam String campaignCategory,
                                                            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignsByCategoryName(campaignCategory, pageRequest);
    }

    @GetMapping("/campaign-name")
    public Page<CampaignResponse> getCampaignByCampaignName(@RequestParam String campaignName,
                                                            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignByName(campaignName, pageRequest);
    }

    @GetMapping("/campaign-name-pending")
    public Page<CampaignResponse> getCampaignByCampaignNamePending(@RequestParam String campaignName,
                                                            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignByNamePending(campaignName, pageRequest);
    }

    @GetMapping("/campaign-name-nonaktif")
    public Page<CampaignResponse> getCampaignByCampaignNameNonaktif(@RequestParam String campaignName,
                                                            @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignByNameNonaktif(campaignName, pageRequest);
    }

    @GetMapping("/emergency")
    public Page<CampaignResponse> getCampaignByEmergency(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignByEmergency(pageRequest);
    }

    @GetMapping("/pending")
    public Page<CampaignResponse> getPendingCampaign(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getPendingCampaign(pageRequest);
    }

    @GetMapping("/history")
    public Page<CampaignResponse> getHistoryCampaign(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getHistoryCampaign(pageRequest);
    }

    @GetMapping("/get-by-operator")
    public Page<CampaignResponse> getCampaignsByOperator(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getCampaignsByOperator(pageRequest);
    }

    @GetMapping("/get-by-operator/active-approve")
    public Page<CampaignResponse> getActiveApproveCampaignsByOperator(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getActiveApproveCampaignsByOperator(pageRequest);
    }

    @GetMapping("/get-by-operator/pending")
    public Page<CampaignResponse> getPendingCampaignsByOperator(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getPendingCampaignsByOperator(pageRequest);
    }

    @GetMapping("/get-by-operator/history")
    public Page<CampaignResponse> getHistoryCampaignsByOperator(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return campaignService.getHistoryCampaignsByOperator(pageRequest);
    }
}
