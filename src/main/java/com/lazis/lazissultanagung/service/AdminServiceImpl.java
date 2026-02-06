package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.EditProfileDonaturRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Admin getCurrentAdmin() throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Admin/operator tidak ditemukan"));
        }
        throw new BadRequestException("Admin/operator tidak ditemukan");
    }

    @Override
    public Page<Map<String, Object>> getAllOperator(Pageable pageable) {
        Page<Object[]> results = adminRepository.getAllOperator(pageable);

        return results.map(row -> {
            Long id = (Long) row [0];
            String username = (String) row[1];
            String email = (String) row[2];
            String phoneNumber = (String) row[3];
            String address = (String) row[4];
            Timestamp timestamp = (Timestamp) row[5];
            boolean status = (boolean) row[6];
            LocalDateTime createdAt = timestamp.toLocalDateTime();

            Map<String, Object> operatorData = new HashMap<>();
            operatorData.put("id", id);
            operatorData.put("username", username);
            operatorData.put("email", email);
            operatorData.put("phoneNumber", phoneNumber);
            operatorData.put("address", address);
            operatorData.put("createdAt", createdAt);
            operatorData.put("status", status);

            return operatorData;
        });
    }

    @Override
    public Admin editProfileAdmin(EditProfileDonaturRequest editProfileRequest) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Akun tidak ditemukan"));

            if (editProfileRequest.getUsername() != null) {
                existingAdmin.setUsername(editProfileRequest.getUsername());
            }
            if (editProfileRequest.getPhoneNumber() != null) {
                existingAdmin.setPhoneNumber(editProfileRequest.getPhoneNumber());
            }
            if (editProfileRequest.getEmail() != null) {
                existingAdmin.setEmail(editProfileRequest.getEmail());
            }
            if (editProfileRequest.getPassword() != null) {
                existingAdmin.setPassword(encoder.encode(editProfileRequest.getPassword()));
            }
            if (editProfileRequest.getAddress() != null) {
                existingAdmin.setAddress(editProfileRequest.getAddress());
            }

            if (editProfileRequest.getImage() != null && !editProfileRequest.getImage().isEmpty()) {
                String imageUrl = cloudinaryService.upload(editProfileRequest.getImage());
                if (imageUrl != null) {
                    existingAdmin.setImage(imageUrl);
                }
            }

            return adminRepository.save(existingAdmin);
        }
        throw new BadRequestException("Akun tidak ditemukan");
    }

    @Override
    public ResponseMessage nonaktiveOperator(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak ditemukan");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("admin tidak ditemukan"));

        Admin nonaktiveOperator = adminRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

        nonaktiveOperator.setActive(false);
        adminRepository.save(nonaktiveOperator);

        return new ResponseMessage(true, "Operator Berhasil Ditutup");
    }

    @Override
    public ResponseMessage aktiveOperator(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak ditemukan");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Admin existingAdmin = adminRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("admin tidak ditemukan"));

        Admin nonaktiveOperator = adminRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Operator tidak ditemukan"));

        nonaktiveOperator.setActive(true);
        adminRepository.save(nonaktiveOperator);

        return new ResponseMessage(true, "Operator Berhasil diaktifkan");
    }

    @Override
    public Page<Map<String, Object>> getActiveOperator(Pageable pageable) {
        Page<Object[]> results = adminRepository.getActiveOperator(pageable);

        return results.map(row -> {
            String username = (String) row[0];
            String email = (String) row[1];
            String phoneNumber = (String) row[2];
            String address = (String) row[3];
            Timestamp timestamp = (Timestamp) row[4];
            LocalDateTime createdAt = timestamp.toLocalDateTime();

            Map<String, Object> operatorData = new HashMap<>();
            operatorData.put("username", username);
            operatorData.put("email", email);
            operatorData.put("phoneNumber", phoneNumber);
            operatorData.put("address", address);
            operatorData.put("createdAt", createdAt);

            return operatorData;
        });
    }

    @Override
    public Page<Admin> searchOperators(String search, Pageable pageable) {
        return adminRepository.searchOperator(search, pageable);
    }

    @Override
    public Page<Map<String, Object>> getAllKeuangan(Pageable pageable) {
        Page<Object[]> results = adminRepository.getAllKeuangan(pageable);

        return results.map(row -> {
            Long id = (Long) row [0];
            String username = (String) row[1];
            String email = (String) row[2];
            String phoneNumber = (String) row[3];
            String address = (String) row[4];
            Timestamp timestamp = (Timestamp) row[5];
            boolean status = (boolean) row[6];
            LocalDateTime createdAt = timestamp.toLocalDateTime();

            Map<String, Object> operatorData = new HashMap<>();
            operatorData.put("id", id);
            operatorData.put("username", username);
            operatorData.put("email", email);
            operatorData.put("phoneNumber", phoneNumber);
            operatorData.put("address", address);
            operatorData.put("createdAt", createdAt);
            operatorData.put("status", status);

            return operatorData;
        });
    }

}
