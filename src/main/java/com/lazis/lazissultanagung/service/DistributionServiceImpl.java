package com.lazis.lazissultanagung.service;

import com.cloudinary.Cloudinary;
import com.lazis.lazissultanagung.dto.request.DistributionRequest;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.*;
import com.lazis.lazissultanagung.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DistributionServiceImpl implements DistributionService{

    @Autowired
    private DistributionRepository distributionRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ZakatRepository zakatRepository;

    @Autowired
    private InfakRepository infakRepository;

    @Autowired
    private DSKLRepository dsklRepository;

    @Autowired
    private WakafRepository wakafRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Page<Distribution> getAllDistributions(Integer month, Integer year, Pageable pageable) {
        return distributionRepository.findAllByMonthAndYear(month, year, pageable)
                .map(distribution -> {
                    Object categoryData = getCategoryData(distribution);
                    // Return distribution with additional data if needed
                    return distribution;
                });
    }

    @Override
    public List<Distribution> getDistributionsByCategoryAndId(String category, Long id) {
        switch (category.toLowerCase()) {
            case "campaign":
                return distributionRepository.findByCategoryAndCampaign_CampaignId(category, id);
            case "zakat":
                return distributionRepository.findByCategoryAndZakat_Id(category, id);
            case "infak":
                return distributionRepository.findByCategoryAndInfak_Id(category, id);
            case "wakaf":
                return distributionRepository.findByCategoryAndWakaf_Id(category, id);
            case "dskl":
                return distributionRepository.findByCategoryAndDskl_Id(category, id);
            default:
                throw new IllegalArgumentException("Invalid category type: " + category);
        }
    }

    @Override
    public Distribution createDistribution(String categoryType, Long id, DistributionRequest distributionRequest) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin not found"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Only ADMIN and Operator can Distribute");
            }

            Distribution distribution = modelMapper.map(distributionRequest, Distribution.class);

            switch (categoryType) {
                case "campaign":
                    Campaign campaign = campaignRepository.findById(id)
                            .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
                    distribution.setCampaign(campaign);
                    break;
                case "zakat":
                    Zakat zakat = zakatRepository.findById(id)
                            .orElseThrow(() -> new BadRequestException("Zakat tidak ditemukan"));
                    distribution.setZakat(zakat);
                    break;
                case "infak":
                    Infak infak = infakRepository.findById(id)
                            .orElseThrow(() -> new BadRequestException("Infak tidak ditemukan"));
                    distribution.setInfak(infak);
                    break;
                case "dskl":
                    DSKL dskl = dsklRepository.findById(id)
                            .orElseThrow(() -> new BadRequestException("DSKL tidak ditemukan"));
                    distribution.setDskl(dskl);
                    break;
                case "wakaf":
                    Wakaf wakaf = wakafRepository.findById(id)
                            .orElseThrow(() -> new BadRequestException("Wakaf tidak ditemukan"));
                    distribution.setWakaf(wakaf);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid category type: " + categoryType);
            }

            String imageUrl = null;
            if (distributionRequest.getImage() != null && !distributionRequest.getImage().isEmpty()) {
                imageUrl = fileStorageService.saveFile(distributionRequest.getImage());
            }
            distribution.setImage("https://skyconnect.lazis-sa.org/api/images/" + imageUrl);
            distribution.setCategory(categoryType);
            distribution.setSuccess(true);

            distribution = distributionRepository.save(distribution);

            // Update current amount in the respective category
            switch (categoryType) {
                case "campaign":
                    campaignRepository.updateCampaignDistribution(id, distributionRequest.getDistributionAmount());
                    break;
                case "zakat":
                    zakatRepository.updateZakatDistribution(id, distributionRequest.getDistributionAmount());
                    break;
                case "infak":
                    infakRepository.updateInfakDistribution(id, distributionRequest.getDistributionAmount());
                    break;
                case "dskl":
                    dsklRepository.updateDSKLDistribution(id, distributionRequest.getDistributionAmount());
                    break;
                case "wakaf":
                    wakafRepository.updateWakafDistribution(id, distributionRequest.getDistributionAmount());
                    break;
            }

            return distribution;
        }
        throw new BadRequestException("Authentication failed");
    }

    private Object getCategoryData(Distribution distribution) {
        switch (distribution.getCategory()) {
            case "zakat":
                return distribution.getZakat();
            case "infak":
                return distribution.getInfak();
            case "wakaf":
                return distribution.getWakaf();
            case "dskl":
                return distribution.getDskl();
            case "campaign":
                return distribution.getCampaign();
            default:
                return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllPenerimaManfaat() {
        List<Object[]> results = distributionRepository.findAllReceiversAndDateAndImage();

        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("receiver", row[0]);
            map.put("distributionDate", row[1]);
            map.put("image", row[2]);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllDistributionswkwk() {
        List<Distribution> distributions = distributionRepository
                .findAll(Sort.by(Sort.Direction.DESC, "distributionDate"));

        return distributions.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            if (d.getCampaign() != null) {
                map.put("name", d.getCampaign().getCampaignName());
            } else if (d.getZakat() != null) {
                map.put("name", d.getZakat().getCategoryName());
            } else if (d.getInfak() != null) {
                map.put("name", d.getInfak().getCategoryName());
            } else if (d.getDskl() != null) {
                map.put("name", d.getDskl().getCategoryName());
            } else if (d.getWakaf() != null) {
                map.put("name", d.getWakaf().getCategoryName());
            } else {
                map.put("name", null);
            }
            map.put("distributionDate", d.getDistributionDate());
            map.put("amount", (long) d.getDistributionAmount());
            map.put("description", d.getDescription());
            map.put("image", d.getImage());



            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Distribution updateDistribution(Long distributionId, DistributionRequest distributionRequest) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin not found"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Only ADMIN and Operator can edit Distribution");
            }

            // Ambil distribusi lama
            Distribution existingDistribution = distributionRepository.findById(distributionId)
                    .orElseThrow(() -> new BadRequestException("Distribution tidak ditemukan"));

            double oldAmount = existingDistribution.getDistributionAmount();

            // Update deskripsi (kalau ada)
            if (distributionRequest.getDescription() != null) {
                existingDistribution.setDescription(distributionRequest.getDescription());
            }

            if (distributionRequest.getReceiver() != null){
                existingDistribution.setReceiver(distributionRequest.getReceiver());
            }

            // Update nominal (kalau ada di request)
            if (distributionRequest.getDistributionAmount() != null) {
                double newAmount = distributionRequest.getDistributionAmount();

                if (Double.compare(newAmount, oldAmount) != 0) {
                    double selisih = newAmount - oldAmount;
                    existingDistribution.setDistributionAmount(newAmount);

                    // update kategori sesuai selisih
                    switch (existingDistribution.getCategory()) {
                        case "campaign":
                            campaignRepository.updateCampaignDistribution(existingDistribution.getCampaign().getCampaignId(), selisih);
                            break;
                        case "zakat":
                            zakatRepository.updateZakatDistribution(existingDistribution.getZakat().getId(), selisih);
                            break;
                        case "infak":
                            infakRepository.updateInfakDistribution(existingDistribution.getInfak().getId(), selisih);
                            break;
                        case "dskl":
                            dsklRepository.updateDSKLDistribution(existingDistribution.getDskl().getId(), selisih);
                            break;
                        case "wakaf":
                            wakafRepository.updateWakafDistribution(existingDistribution.getWakaf().getId(), selisih);
                            break;
                    }
                }
            }

            if (distributionRequest.getImage() != null && !distributionRequest.getImage().isEmpty()) {
                String imageUrl = fileStorageService.saveFile(distributionRequest.getImage());
                existingDistribution.setImage("https://skyconnect.lazis-sa.org/api/images/" + imageUrl);
            }


            return distributionRepository.save(existingDistribution);
        }
        throw new BadRequestException("Authentication failed");
    }

    @Override
    public Distribution getDistributionById(Long distributionId) throws BadRequestException {
        return distributionRepository.findById(distributionId)
                .orElseThrow(() -> new BadRequestException("Distribution tidak ditemukan"));
    }


}
