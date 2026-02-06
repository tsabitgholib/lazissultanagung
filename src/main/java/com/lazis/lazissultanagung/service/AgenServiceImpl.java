package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lazis.lazissultanagung.repository.AgenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AgenServiceImpl implements AgenService {

    @Autowired
    private AgenRepository agenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Agen> getAllAgen() {
        return agenRepository.findAll();
    }

    @Override
    public Agen createAgen(AgenRequest agenRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak terautentikasi");
        }
        if (agenRepository.existsByEmail(agenRequest.getEmail())) {
            throw new BadRequestException("Error: Email sudah digunakan!");
        }

        if (agenRepository.existsByPhoneNumber(agenRequest.getPhoneNumber())) {
            throw new BadRequestException("Error: Nomor Handphone sudah digunakan!");
        }

        Agen agen = new Agen();
        agen.setName(agenRequest.getName());
        agen.setPhoneNumber(agenRequest.getPhoneNumber());
        agen.setEmail(agenRequest.getEmail());
        agen.setAddress(agenRequest.getAddress());
        agen.setPassword(passwordEncoder.encode(agenRequest.getPassword()));
        agen.setCreatedAt(new Date());
        agen.setUpdatedAt(new Date());

        return agenRepository.save(agen);
    }

    @Override
    public Agen getAgenById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak terautentikasi");
        }
        return agenRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Agen not found with id " + id));
    }

    @Override
    public Agen updateAgen(Long id, AgenRequest agenRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak terautentikasi");
        }
        Agen existingAgen = getAgenById(id);

        if (agenRequest.getName() != null) {
            existingAgen.setName(agenRequest.getName());
        }

        if (agenRequest.getPhoneNumber() != null) {
             if (!existingAgen.getPhoneNumber().equals(agenRequest.getPhoneNumber()) && agenRepository.existsByPhoneNumber(agenRequest.getPhoneNumber())) {
                 throw new BadRequestException("Error: Nomor Handphone sudah digunakan!");
             }
            existingAgen.setPhoneNumber(agenRequest.getPhoneNumber());
        }

        if (agenRequest.getEmail() != null) {
             if (!existingAgen.getEmail().equals(agenRequest.getEmail()) && agenRepository.existsByEmail(agenRequest.getEmail())) {
                 throw new BadRequestException("Error: Email sudah digunakan!");
             }
            existingAgen.setEmail(agenRequest.getEmail());
        }

        if (agenRequest.getAddress() != null) {
            existingAgen.setAddress(agenRequest.getAddress());
        }

        if (agenRequest.getPassword() != null && !agenRequest.getPassword().isEmpty()) {
            existingAgen.setPassword(passwordEncoder.encode(agenRequest.getPassword()));
        }

        existingAgen.setUpdatedAt(new Date());

        return agenRepository.save(existingAgen);
    }

    @Override
    public void deleteAgen(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BadRequestException("Admin tidak terautentikasi");
        }
        Agen agen = getAgenById(id);
        agenRepository.delete(agen);
    }
}
