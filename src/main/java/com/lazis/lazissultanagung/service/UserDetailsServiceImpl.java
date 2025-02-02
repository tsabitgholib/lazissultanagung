package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Donatur;
import com.lazis.lazissultanagung.repository.AdminRepository;
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

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<Donatur> donaturOptional = donaturRepository.findByPhoneNumber(input);
        Optional<Admin> adminOptional = adminRepository.findByPhoneNumber(input);

        if (donaturOptional.isPresent()) {
            Donatur donatur = donaturOptional.get();
            return UserDetailsImpl.build(donatur);
        }

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return UserDetailsImpl.build(admin);
        }

        donaturOptional = donaturRepository.findByEmail(input);
        adminOptional = adminRepository.findByEmail(input);

        if (donaturOptional.isPresent()) {
            Donatur donatur = donaturOptional.get();
            return UserDetailsImpl.build(donatur);
        }

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return UserDetailsImpl.build(admin);
        }

        throw new UsernameNotFoundException("User tidak ditemukan dengan inputan: " + input);
    }
}
