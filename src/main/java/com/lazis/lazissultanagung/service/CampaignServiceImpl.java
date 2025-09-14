package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.CampaignRequest;
import com.lazis.lazissultanagung.dto.response.CampaignResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Campaign;
import com.lazis.lazissultanagung.model.CampaignCategory;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.CampaignCategoryRepository;
import com.lazis.lazissultanagung.repository.CampaignRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignCategoryRepository campaignCategoryRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public CampaignResponse createCampaign(CampaignRequest campaignRequest) {
        CampaignCategory campaignCategory = campaignCategoryRepository.findById(campaignRequest.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Kategori Tidak ditemukan"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.isActive()) {
                throw new BadRequestException("Akun Operator anda Nonaktif!");
            }

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa membuat campaign");
            }

            String imageUrl = null;
            if (campaignRequest.getCampaignImage() != null && !campaignRequest.getCampaignImage().isEmpty()) {
                imageUrl = fileStorageService.saveFile(campaignRequest.getCampaignImage());
            }
            String imagedesc1Url = null;
            if (campaignRequest.getCampaignImageDesc1() != null && !campaignRequest.getCampaignImageDesc1().isEmpty()) {
                imagedesc1Url = fileStorageService.saveFile(campaignRequest.getCampaignImageDesc1());
            }
            String imagedesc2Url = null;
            if (campaignRequest.getCampaignImageDesc2() != null && !campaignRequest.getCampaignImageDesc2().isEmpty()) {
                imagedesc2Url = fileStorageService.saveFile(campaignRequest.getCampaignImageDesc2());
            }
            String imagedesc3Url = null;
            if (campaignRequest.getCampaignImageDesc3() != null && !campaignRequest.getCampaignImageDesc3().isEmpty()) {
                imagedesc3Url = fileStorageService.saveFile(campaignRequest.getCampaignImageDesc3());
            }

            Campaign campaign = new Campaign();
            campaign.setCampaignCategory(campaignCategory);
            campaign.setCampaignName(campaignRequest.getCampaignName());
            campaign.setCampaignCode(campaignRequest.getCampaignCode());
            campaign.setCampaignImage(imageUrl);
            campaign.setCampaignImageDesc1(imagedesc1Url);
            campaign.setCampaignImageDesc2(imagedesc2Url);
            campaign.setCampaignImageDesc3(imagedesc3Url);
            campaign.setDescription(campaignRequest.getDescription());
            campaign.setLocation(campaignRequest.getLocation());
            campaign.setTargetAmount(campaignRequest.getTargetAmount());
            campaign.setCurrentAmount(0.0);
            campaign.setStartDate(campaignRequest.getStartDate());
            campaign.setEndDate(campaignRequest.getEndDate());
            campaign.setActive(true);
            campaign.setAdmin(existingAdmin);
            campaign.setDistribution(0.0);
            if (existingAdmin.getRole().equals(ERole.ADMIN)) {
                campaign.setApproved(true);
            } else if (existingAdmin.getRole().equals(ERole.OPERATOR)) {
                campaign.setApproved(false);
            }
            campaign.setEmergency(campaignRequest.isEmergency());

            Campaign savedCampaign = campaignRepository.save(campaign);

            CampaignResponse campaignResponse = modelMapper.map(savedCampaign, CampaignResponse.class);

            campaignResponse.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            campaignResponse.setCreator(existingAdmin.getUsername());

            return campaignResponse;
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public CampaignResponse editCampaign(Long id, CampaignRequest campaignRequest) {
        // Mencari campaign berdasarkan ID
        Campaign existingCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));

        // Mencari kategori campaign
        CampaignCategory campaignCategory = campaignCategoryRepository.findById(campaignRequest.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Kategori Tidak ditemukan"));

        // Mendapatkan informasi admin yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.isActive()) {
                throw new BadRequestException("Akun Operator anda Nonaktif!");
            }

            if (!existingAdmin.getRole().equals(ERole.ADMIN) && !existingAdmin.getRole().equals(ERole.OPERATOR)) {
                throw new BadRequestException("Hanya Admin dan Operator yang bisa mengedit campaign");
            }

            // Mengunggah gambar campaign jika ada
            String imageUrl = existingCampaign.getCampaignImage(); // gambar lama
            if (campaignRequest.getCampaignImage() != null && !campaignRequest.getCampaignImage().isEmpty()) {
                imageUrl = fileStorageService.saveFile(campaignRequest.getCampaignImage());
            }

            String imagedesc1Url = null;
            if (campaignRequest.getCampaignImageDesc1() != null && !campaignRequest.getCampaignImageDesc1().isEmpty()) {
                imagedesc1Url = fileStorageService.saveFile(campaignRequest.getCampaignImageDesc1());
            }
            String imagedesc2Url = null;
            if (campaignRequest.getCampaignImageDesc2() != null && !campaignRequest.getCampaignImageDesc2().isEmpty()) {
                imagedesc2Url = fileStorageService.saveFile(campaignRequest.getCampaignImageDesc2());
            }
            String imagedesc3Url = null;
            if (campaignRequest.getCampaignImageDesc3() != null && !campaignRequest.getCampaignImageDesc3().isEmpty()) {
                imagedesc3Url = fileStorageService.saveFile(campaignRequest.getCampaignImageDesc3());
            }


            // Mengupdate field campaign yang diperbolehkan
            existingCampaign.setCampaignCategory(campaignCategory);
            existingCampaign.setCampaignName(campaignRequest.getCampaignName());
            existingCampaign.setCampaignCode(campaignRequest.getCampaignCode());
            existingCampaign.setCampaignImage(imageUrl);
            existingCampaign.setCampaignImageDesc1(imagedesc1Url);
            existingCampaign.setCampaignImageDesc2(imagedesc2Url);
            existingCampaign.setCampaignImageDesc3(imagedesc3Url);
            existingCampaign.setDescription(campaignRequest.getDescription());
            existingCampaign.setLocation(campaignRequest.getLocation());
            existingCampaign.setTargetAmount(campaignRequest.getTargetAmount());
            existingCampaign.setEmergency(campaignRequest.isEmergency());

            // Simpan campaign yang sudah diubah
            Campaign updatedCampaign = campaignRepository.save(existingCampaign);

            // Map ke response
            CampaignResponse campaignResponse = modelMapper.map(updatedCampaign, CampaignResponse.class);
            campaignResponse.setCampaignCategory(updatedCampaign.getCampaignCategory().getCampaignCategory());
            campaignResponse.setCreator(existingAdmin.getUsername());

            return campaignResponse;
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public List<CampaignResponse> getAllCampaign() {
        List<Campaign> campaigns = campaignRepository.findAll();
        return campaigns.stream()
                .map(campaign -> {
                    CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
                    response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
                    response.setCreator(campaign.getAdmin().getUsername()); // Set creator's username
                    response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CampaignResponse> getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .map(campaign -> {
                    CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
                    response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
                    response.setCreator(campaign.getAdmin().getUsername());
                    response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

                    return response;
                });
    }


    @Override
    public ResponseMessage deleteCampaign(Long id) {
        Campaign deleteCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
        campaignRepository.delete(deleteCampaign);
        return new ResponseMessage(true, "Campaign Berhasil Dihapus");
    }

    @Override
    public ResponseMessage closeCampaign(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak terautentikasi");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("admin tidak ditemukan"));

        Campaign closedCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));
        closedCampaign.setActive(false);
        campaignRepository.save(closedCampaign);
        return new ResponseMessage(true, "Campaign Berhasil Ditutup");
    }

    @Override
    @Transactional
    public ResponseMessage approveCampaign(Long id) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN)) {
                throw new BadRequestException("Hanya Admin yang bisa menyetujui campaign");
            }

            Campaign campaign = campaignRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("Campaign tidak ditemukan"));

            campaign.setApproved(true);
            campaignRepository.save(campaign);

            return new ResponseMessage(true, "Campaign berhasil disetujui");
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public Page<CampaignResponse> getCampaignByActiveAndApproved(Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findCampaignByActiveAndApproved(pageable);

        // Update setiap Campaign jika `currentAmount` >= `targetAmount`
        campaigns.forEach(campaign -> {
            if (campaign.getCurrentAmount() >= campaign.getTargetAmount()) {
                campaign.setActive(false);
                campaignRepository.save(campaign);
            }
        });

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);

        // Menerapkan mapping dari Campaign ke CampaignResponse
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getCampaignsByCategoryName(String campaignCategory, Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findByCategoryName(campaignCategory, pageable);
        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);

        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getCampaignByName(String campaignName, Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findByCampaignName(campaignName, pageable);

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getCampaignByNamePending(String campaignName, Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findByCampaignNamePending(campaignName, pageable);

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getCampaignByNameNonaktif(String campaignName, Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findByCampaignNameNonaktif(campaignName, pageable);

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getCampaignByEmergency(Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findCampaignByEmergency(pageable);

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getPendingCampaign(Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findPendingCampaign(pageable);

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getHistoryCampaign(Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findHistoryCampaign(pageable);

        // Hitung offset berdasarkan halaman saat ini dan ukuran halaman
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        // Membuat counter yang mulai dari offset + 1
        AtomicInteger counter = new AtomicInteger(offset + 1);
        return campaigns.map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

            return response;
        });
    }

    @Override
    public Page<CampaignResponse> getCampaignsByOperator(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin operator = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

            Page<Campaign> campaigns = campaignRepository.findByAdmin(operator, pageable);

            // Calculate offset and create counter
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            AtomicInteger counter = new AtomicInteger(offset + 1);

            return campaigns.map(campaign -> {
                CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
                response.setDisplayId(counter.getAndIncrement());
                response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
                response.setCreator(campaign.getAdmin().getUsername());
                response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

                return response;
            });
        } else {
            throw new BadRequestException("Operator tidak terautentikasi");
        }
    }

    @Override
    public Page<CampaignResponse> getActiveApproveCampaignsByOperator(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin operator = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

            Page<Campaign> campaigns = campaignRepository.findActiveApproveCampaignOperator(operator, pageable);

            // Calculate offset and create counter
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            AtomicInteger counter = new AtomicInteger(offset + 1);

            return campaigns.map(campaign -> {
                CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
                response.setDisplayId(counter.getAndIncrement());
                response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
                response.setCreator(campaign.getAdmin().getUsername());
                response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

                return response;
            });
        } else {
            throw new BadRequestException("Operator tidak terautentikasi");
        }
    }

    @Override
    public Page<CampaignResponse> getPendingCampaignsByOperator(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin operator = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

            Page<Campaign> campaigns = campaignRepository.findPendingCampaignOperator(operator, pageable);

            // Calculate offset and create counter
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            AtomicInteger counter = new AtomicInteger(offset + 1);

            return campaigns.map(campaign -> {
                CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
                response.setDisplayId(counter.getAndIncrement());
                response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
                response.setCreator(campaign.getAdmin().getUsername());
                response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

                return response;
            });
        } else {
            throw new BadRequestException("Operator tidak terautentikasi");
        }
    }

    @Override
    public Page<CampaignResponse> getHistoryCampaignsByOperator(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin operator = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

            Page<Campaign> campaigns = campaignRepository.findHistoryCampaignOperator(operator, pageable);

            // Calculate offset and create counter
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            AtomicInteger counter = new AtomicInteger(offset + 1);

            return campaigns.map(campaign -> {
                CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
                response.setDisplayId(counter.getAndIncrement());
                response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
                response.setCreator(campaign.getAdmin().getUsername());
                response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/"+campaign.getCampaignImage());

                return response;
            });
        } else {
            throw new BadRequestException("Operator tidak terautentikasi");
        }
    }

    @Override
    public List<CampaignResponse> getCampaignByPriority() {
        List<Campaign> campaigns = campaignRepository.findCampaignByPriority();

        return campaigns.stream().map(campaign -> {
            CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
            // response.setDisplayId(counter.getAndIncrement());
            response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
            response.setCreator(campaign.getAdmin().getUsername());
            response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/" + campaign.getCampaignImage());

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CampaignResponse setCampaignPriority(Long campaignId, boolean priority) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign tidak ditemukan"));

        campaign.setPriority(priority); // update priority
        campaignRepository.save(campaign);

        // Mapping ke response
        CampaignResponse response = modelMapper.map(campaign, CampaignResponse.class);
        response.setCampaignCategory(campaign.getCampaignCategory().getCampaignCategory());
        response.setCreator(campaign.getAdmin().getUsername());
        response.setCampaignImage("https://skyconnect.lazis-sa.org/api/images/" + campaign.getCampaignImage());

        return response;
    }




}