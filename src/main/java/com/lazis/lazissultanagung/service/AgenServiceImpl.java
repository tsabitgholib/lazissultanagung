package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.repository.AgenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AgenServiceImpl implements AgenService {

    @Autowired
    private AgenRepository agenRepository;

    @Autowired
    private PasswordEncoder encoder;

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

    @Override
    public Agen createAgen(AgenRequest agenRequest) throws BadRequestException {
        if (agenRepository.existsByEmail(agenRequest.getEmail())) {
            throw new BadRequestException("Error: Email sudah digunakan!");
        }

        if (agenRepository.existsByPhoneNumber(agenRequest.getPhoneNumber())) {
            throw new BadRequestException("Error: Nomor Handphone sudah digunakan!");
        }

        Agen agen = new Agen(
                agenRequest.getUsername(),
                agenRequest.getPhoneNumber(),
                agenRequest.getEmail(),
                encoder.encode(agenRequest.getPassword()),
                agenRequest.getAddress()
        );
        
        agen.setTargetAmount(agenRequest.getTargetAmount());
        
        return agenRepository.save(agen);
    }

    @Override
    public Agen updateAgen(Long id, AgenRequest agenRequest) throws BadRequestException {
        Agen agen = agenRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Agen tidak ditemukan"));

        if (agenRequest.getUsername() != null) agen.setUsername(agenRequest.getUsername());
        if (agenRequest.getPhoneNumber() != null) agen.setPhoneNumber(agenRequest.getPhoneNumber());
        if (agenRequest.getEmail() != null) agen.setEmail(agenRequest.getEmail());
        if (agenRequest.getPassword() != null) agen.setPassword(encoder.encode(agenRequest.getPassword()));
        if (agenRequest.getAddress() != null) agen.setAddress(agenRequest.getAddress());
        if (agenRequest.getTargetAmount() != null) agen.setTargetAmount(agenRequest.getTargetAmount());
        
        agen.setUpdatedAt(new Date());

        return agenRepository.save(agen);
    }

    @Override
    public void deleteAgen(Long id) throws BadRequestException {
        if (!agenRepository.existsById(id)) {
            throw new BadRequestException("Agen tidak ditemukan");
        }
        agenRepository.deleteById(id);
    }

    @Override
    public Agen getAgenById(Long id) throws BadRequestException {
        return agenRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Agen tidak ditemukan"));
    }

    @Override
    public Page<Agen> getAllAgen(Pageable pageable) {
        return agenRepository.findAll(pageable);
    }
}
