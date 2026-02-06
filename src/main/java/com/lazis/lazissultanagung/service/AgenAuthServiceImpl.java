package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.SignInRequest;
import com.lazis.lazissultanagung.dto.response.JwtResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.repository.AgenRepository;
import com.lazis.lazissultanagung.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AgenAuthServiceImpl implements AgenAuthService {

    @Autowired
    private AgenRepository agenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateAgen(SignInRequest signinRequest) throws BadRequestException {
        Agen agen = agenRepository.findByEmail(signinRequest.getEmailOrPhoneNumber())
                .or(() -> agenRepository.findByPhoneNumber(signinRequest.getEmailOrPhoneNumber()))
                .orElseThrow(() -> new BadRequestException("Email atau nomor handphone belum terdaftar sebagai agen"));

        if (!passwordEncoder.matches(signinRequest.getPassword(), agen.getPassword())) {
            throw new BadRequestException("Password anda salah");
        }

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_AGEN"));

        UserDetailsImpl userDetails = new UserDetailsImpl(
                agen.getId(),
                agen.getUsername(),
                agen.getPhoneNumber(),
                agen.getEmail(),
                agen.getPassword(),
                authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        String jwt = jwtUtils.generateJwtToken(authentication);

        return new JwtResponse(userDetails.getUsername(), jwt);
    }
}
