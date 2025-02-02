package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.CampaignCategory;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.CampaignCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignCategoryServiceImpl implements CampaignCategoryService {

    @Autowired
    private CampaignCategoryRepository campaignCategoryRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public List<CampaignCategory> getAllCampaignCategory() {
        return campaignCategoryRepository.findAll();
    }

    @Override
    public CampaignCategory createCampaignCategory(CampaignCategory campaignCategory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat kategori");
            }

            // Convert campaignCategory name to title case
            String formattedName = toTitleCase(campaignCategory.getCampaignCategory());
            campaignCategory.setCampaignCategory(formattedName);

            return campaignCategoryRepository.save(campaignCategory);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public CampaignCategory updateCampaignCategory(Long id, CampaignCategory campaignCategory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa mengedit kategori");
            }

            CampaignCategory updateCategory = campaignCategoryRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("Category tidak ditemukan"));

            // Convert campaignCategory name to title case
            String formattedName = toTitleCase(campaignCategory.getCampaignCategory());
            updateCategory.setCampaignCategory(formattedName);
            return campaignCategoryRepository.save(updateCategory);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public ResponseMessage deleteCampaignCategory(Long id){
        CampaignCategory deleteCategory = campaignCategoryRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("Category tidak ditemukan"));

        campaignCategoryRepository.delete(deleteCategory);

        return new ResponseMessage(true, "Category berhasil dihapus");
    }

    // Helper method to convert a string to title case
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.toLowerCase().split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (word.length() > 1) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            } else {
                titleCase.append(Character.toUpperCase(word.charAt(0))).append(" ");
            }
        }
        return titleCase.toString().trim();
    }
}
