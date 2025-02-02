package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.EditProfileDonaturRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Donatur;
import com.lazis.lazissultanagung.repository.DonaturRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DonaturServiceImpl implements DonaturService {

    @Autowired
    private DonaturRepository donaturRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CloudinaryService cloudinaryService;


    @Override
    public Donatur getCurrentDonatur() throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Jika nomor telepon tidak tersedia (kosong), gunakan email untuk pencarian
            if (userDetails.getPhoneNumber() == null || userDetails.getPhoneNumber().isEmpty()) {
                return donaturRepository.findByEmail(userDetails.getEmail())
                        .orElseThrow(() -> new BadRequestException("Donatur tidak ditemukan berdasarkan email"));
            } else {
                // Jika nomor telepon ada, lakukan pencarian berdasarkan nomor telepon
                return donaturRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                        .orElseThrow(() -> new BadRequestException("Donatur tidak ditemukan berdasarkan nomor telepon"));
            }
        }

        throw new BadRequestException("Donatur tidak ditemukan");
    }


    @Override
    public Donatur editProfileDonatur(EditProfileDonaturRequest editProfileRequest) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Donatur existingDonatur = donaturRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                    .orElseThrow(() -> new BadRequestException("Donatur tidak ditemukan"));

            if (editProfileRequest.getUsername() != null) {
                existingDonatur.setUsername(editProfileRequest.getUsername());
            }
            if (editProfileRequest.getPhoneNumber() != null) {
                existingDonatur.setPhoneNumber(editProfileRequest.getPhoneNumber());
            }
            if (editProfileRequest.getEmail() != null) {
                existingDonatur.setEmail(editProfileRequest.getEmail());
            }
            if (editProfileRequest.getPassword() != null) {
                existingDonatur.setPassword(encoder.encode(editProfileRequest.getPassword()));
            }
            if (editProfileRequest.getAddress() != null) {
                existingDonatur.setAddress(editProfileRequest.getAddress());
            }

            if (editProfileRequest.getImage() != null && !editProfileRequest.getImage().isEmpty()) {
                String imageUrl = cloudinaryService.upload(editProfileRequest.getImage());
                if (imageUrl != null) {
                    existingDonatur.setImage(imageUrl);
                }
            }

            return donaturRepository.save(existingDonatur);
        }
        throw new BadRequestException("Donatur tidak ditemukan");
    }

    @Override
    public Page<Donatur> getAllDonatur(Pageable pageable){
        return donaturRepository.getAllDonatur(pageable);
    }

    @Override
    public Page<Donatur> searchDonaturs(String search, Pageable pageable) {
        return donaturRepository.search(search, pageable);
    }


}
