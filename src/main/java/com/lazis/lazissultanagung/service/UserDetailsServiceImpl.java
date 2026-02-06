package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.model.Donatur;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.AgenRepository;
import com.lazis.lazissultanagung.repository.DonaturRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    DonaturRepository donaturRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AgenRepository agenRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<Donatur> donaturOptional = donaturRepository.findByPhoneNumber(input);
        Optional<Admin> adminOptional = adminRepository.findByPhoneNumber(input);
        Optional<Agen> agenOptional = agenRepository.findByPhoneNumber(input);

        if (donaturOptional.isPresent()) {
            Donatur donatur = donaturOptional.get();
            return UserDetailsImpl.build(donatur);
        }

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return UserDetailsImpl.build(admin);
        }

        if (agenOptional.isPresent()) {
            Agen agen = agenOptional.get();
            return UserDetailsImpl.build(agen);
        }

        donaturOptional = donaturRepository.findByEmail(input);
        adminOptional = adminRepository.findByEmail(input);
        agenOptional = agenRepository.findByEmail(input);

        if (donaturOptional.isPresent()) {
            Donatur donatur = donaturOptional.get();
            return UserDetailsImpl.build(donatur);
        }

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return UserDetailsImpl.build(admin);
        }

        if (agenOptional.isPresent()) {
            Agen agen = agenOptional.get();
            return UserDetailsImpl.build(agen);
        }

        throw new UsernameNotFoundException("User tidak ditemukan dengan inputan: " + input);
    }
}
