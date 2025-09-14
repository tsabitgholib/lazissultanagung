package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.DashboardImageRequest;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.DashboardImage;
import com.lazis.lazissultanagung.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardImageServiceImpl implements DashboardImageService{

    @Autowired
    private DashboardImageRepository dashboardImageRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ZakatRepository zakatRepository;

    @Autowired
    private InfakRepository infakRepository;

    @Autowired
    private DSKLRepository dsklRepository;

    @Override
    public List<DashboardImage> getAllDashboardImage() {
        return dashboardImageRepository.findAll().stream().map(dashboardImage -> {
            if (dashboardImage.getImage_1() != null && !dashboardImage.getImage_1().startsWith("http")) {
                dashboardImage.setImage_1("https://skyconnect.lazis-sa.org/api/images/" + dashboardImage.getImage_1());
            }
            if (dashboardImage.getImage_2() != null && !dashboardImage.getImage_2().startsWith("http")) {
                dashboardImage.setImage_2("https://skyconnect.lazis-sa.org/api/images/" + dashboardImage.getImage_2());
            }
            if (dashboardImage.getImage_3() != null && !dashboardImage.getImage_3().startsWith("http")) {
                dashboardImage.setImage_3("https://skyconnect.lazis-sa.org/api/images/" + dashboardImage.getImage_3());
            }
            return dashboardImage;
        }).collect(Collectors.toList());
    }

    @Override
    public DashboardImage addDashboardImage(DashboardImageRequest dashboardImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN)) {
                throw new BadRequestException("Hanya Admin yang bisa menambah gambar");
            }

            DashboardImage dashboardImage = new DashboardImage();

            String image_1url = null;
            if (dashboardImageRequest.getImage_1() != null && !dashboardImageRequest.getImage_1().isEmpty()) {
                image_1url = fileStorageService.saveFile(dashboardImageRequest.getImage_1());
            }
            dashboardImage.setImage_1(image_1url);

            String image_2url = null;
            if (dashboardImageRequest.getImage_2() != null && !dashboardImageRequest.getImage_2().isEmpty()) {
                image_2url = fileStorageService.saveFile(dashboardImageRequest.getImage_2());
            }
            dashboardImage.setImage_2(image_2url);

            String image_3url = null;
            if (dashboardImageRequest.getImage_3() != null && !dashboardImageRequest.getImage_3().isEmpty()) {
                image_3url = fileStorageService.saveFile(dashboardImageRequest.getImage_3());
            }
            dashboardImage.setImage_3(image_3url);

            return dashboardImageRepository.save(dashboardImage);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public DashboardImage editDashboardImage(Long id, DashboardImageRequest dashboardImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN)) {
                throw new BadRequestException("Hanya Admin yang bisa menambah gambar");
            }

            DashboardImage dashboardImage = dashboardImageRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("ID dashboard image tidak ditemukan"));

            if (dashboardImageRequest.getImage_1() != null && !dashboardImageRequest.getImage_1().isEmpty()) {
                String image1Url = fileStorageService.saveFile(dashboardImageRequest.getImage_1());
                dashboardImage.setImage_1(image1Url);
            }

            if (dashboardImageRequest.getImage_2() != null && !dashboardImageRequest.getImage_2().isEmpty()) {
                String image2Url = fileStorageService.saveFile(dashboardImageRequest.getImage_2());
                dashboardImage.setImage_2(image2Url);
            }

            if (dashboardImageRequest.getImage_3() != null && !dashboardImageRequest.getImage_3().isEmpty()) {
                String image3Url = fileStorageService.saveFile(dashboardImageRequest.getImage_3());
                dashboardImage.setImage_3(image3Url);
            }

            return dashboardImageRepository.save(dashboardImage);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public DashboardImage deleteDashboardImage(Long id, List<String> imagesToDelete) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin tidak ditemukan"));

            if (!existingAdmin.getRole().equals(ERole.ADMIN)) {
                throw new BadRequestException("Hanya Admin yang bisa menambah gambar");
            }

            // Ambil data DashboardImage berdasarkan ID
            DashboardImage dashboardImage = dashboardImageRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("ID dashboard image tidak ditemukan"));

            // Hapus gambar sesuai dengan parameter yang diberikan
            if (imagesToDelete.contains("image_1")) {
                if (dashboardImage.getImage_1() != null) {
                    cloudinaryService.delete(dashboardImage.getImage_1());
                    dashboardImage.setImage_1(null);
                }
            }

            if (imagesToDelete.contains("image_2")) {
                if (dashboardImage.getImage_2() != null) {
                    cloudinaryService.delete(dashboardImage.getImage_2());
                    dashboardImage.setImage_2(null);
                }
            }

            if (imagesToDelete.contains("image_3")) {
                if (dashboardImage.getImage_3() != null) {
                    cloudinaryService.delete(dashboardImage.getImage_3());
                    dashboardImage.setImage_3(null);
                }
            }

            // Simpan perubahan ke database
            return dashboardImageRepository.save(dashboardImage);
        }
        throw new BadRequestException("Admin tidak ditemukan");
    }

    @Override
    public List<Map<String, Object>> getAllPenghimpunan() {
        List<Map<String, Object>> hasil = new ArrayList<>();

        campaignRepository.findAllPenghimpunan().forEach(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("amount", ((Double) row[1]) % 1 == 0 ? ((Double) row[1]).longValue() : row[1]);
            hasil.add(map);
        });

        zakatRepository.findAllPenghimpunan().forEach(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("amount", ((Double) row[1]) % 1 == 0 ? ((Double) row[1]).longValue() : row[1]);

            hasil.add(map);
        });

        infakRepository.findAllPenghimpunan().forEach(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("amount", ((Double) row[1]) % 1 == 0 ? ((Double) row[1]).longValue() : row[1]);

            hasil.add(map);
        });

        dsklRepository.findAllPenghimpunan().forEach(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("amount", ((Double) row[1]) % 1 == 0 ? ((Double) row[1]).longValue() : row[1]);

            hasil.add(map);
        });

        return hasil;
    }

}
