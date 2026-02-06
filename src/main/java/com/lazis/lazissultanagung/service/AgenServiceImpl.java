package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.repository.AgenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AgenServiceImpl implements AgenService {

    @Autowired
    private AgenRepository agenRepository;

    @Override
    public Agen getCurrentAgen() throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Jika nomor telepon tidak tersedia (kosong), gunakan email untuk pencarian
            if (userDetails.getPhoneNumber() == null || userDetails.getPhoneNumber().isEmpty()) {
                return agenRepository.findByEmail(userDetails.getEmail())
                        .orElseThrow(() -> new BadRequestException("Agen tidak ditemukan berdasarkan email"));
            } else {
                // Jika nomor telepon ada, lakukan pencarian berdasarkan nomor telepon
                return agenRepository.findByPhoneNumber(userDetails.getPhoneNumber())
                        .orElseThrow(() -> new BadRequestException("Agen tidak ditemukan berdasarkan nomor telepon"));
            }
        }

        throw new BadRequestException("Agen tidak ditemukan atau sesi tidak valid");
    }
}
