package com.lazis.lazissultanagung.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lazis.lazissultanagung.dto.request.ResetPasswordRequest;
import com.lazis.lazissultanagung.dto.response.JwtResponse;
import com.lazis.lazissultanagung.dto.request.SignInRequest;
import com.lazis.lazissultanagung.dto.request.SignUpRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.enumeration.ERole;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Donatur;
import com.lazis.lazissultanagung.repository.AdminRepository;
import com.lazis.lazissultanagung.repository.DonaturRepository;
import com.lazis.lazissultanagung.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DonaturRepository donaturRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public JwtResponse authenticateUser(SignInRequest signinRequest, HttpServletResponse response, String userType) throws BadRequestException {
        Authentication authentication;

        if (userType.equals("ADMIN")) {
            // Mencari user dari adminRepository
            Admin admin = adminRepository.findByEmail(signinRequest.getEmailOrPhoneNumber())
                    .or(() -> adminRepository.findByPhoneNumber(signinRequest.getEmailOrPhoneNumber()))
                    .orElseThrow(() -> new BadRequestException("Email atau nomor handphone belum terdaftar sebagai admin"));

            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(signinRequest.getEmailOrPhoneNumber(), signinRequest.getPassword()));
            } catch (BadCredentialsException e) {
                throw new BadRequestException("Password anda salah cie");
            }

        } else if (userType.equals("DONATUR")) {
            // Mencari user dari donaturRepository
            Donatur donatur = donaturRepository.findByEmail(signinRequest.getEmailOrPhoneNumber())
                    .or(() -> donaturRepository.findByPhoneNumber(signinRequest.getEmailOrPhoneNumber()))
                    .orElseThrow(() -> new BadRequestException("Email atau nomor handphone belum terdaftar sebagai donatur"));

            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(signinRequest.getEmailOrPhoneNumber(), signinRequest.getPassword()));
            } catch (BadCredentialsException e) {
                throw new BadRequestException("Password anda salah");
            }
        } else {
            throw new BadRequestException("Tipe pengguna tidak valid");
        }

        // Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);
        String username = userDetails.getUsername();
        return new JwtResponse(username, jwt);
    }

    @Override
    public Admin registerAdmin(SignUpRequest signUpRequest) throws BadRequestException {
        if (adminRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Error: Email sudah digunakan!");
        }

        if (adminRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new BadRequestException("Error: Nomor Handphone sudah digunakan!");
        }

        Admin admin = new Admin(
                signUpRequest.getUsername(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getAddress(),
                ERole.ADMIN);

        admin.setImage("https://res.cloudinary.com/donation-application/image/upload/v1711632747/default-avatar-icon-of-social-media-user-vector_thrtbz.jpg");

        if (signUpRequest.getRole() != null) {
            if (signUpRequest.getRole().equalsIgnoreCase("admin")) {
                admin.setRole(ERole.ADMIN);
            } else if (signUpRequest.getRole().equalsIgnoreCase("operator")) {
                admin.setRole(ERole.OPERATOR);
            } else if (signUpRequest.getRole().equalsIgnoreCase("keuangan")){
                admin.setRole(ERole.KEUANGAN);
            }
        }

        // Generate nomor VA unik untuk donatur
//        Random random = new Random();
//        long min = 1000000000L;
//        long max = 9999999999L;
//
//        for (int i = 0; i < 10; i++) {
//            long vaNumber = min + (long) (random.nextDouble() * (max - min));
//            admin.setVaNumber(vaNumber);
//        }
        admin.setActive(true);
        adminRepository.save(admin);

        return admin;
    }

    @Override
    public Donatur registerDonatur(SignUpRequest signUpRequest) throws BadRequestException {
        if (donaturRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Error: Email Sudah digunakan!");
        }

        if (donaturRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new BadRequestException("Error: Nomor Handphone sudah digunakan!");
        }

        // Set role default sebagai DONATUR
        Donatur donatur = new Donatur(
                signUpRequest.getUsername(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),  // Encode password
                signUpRequest.getAddress()
        );

        donatur.setImage("https://res.cloudinary.com/donation-application/image/upload/v1711632747/default-avatar-icon-of-social-media-user-vector_thrtbz.jpg");

//        // Generate nomor VA unik
//        Random random = new Random();
//        long min = 1000000000L;
//        long max = 9999999999L;
//
//        for (int i = 0; i < 10; i++) {
//            long vaNumber = min + (long) (random.nextDouble() * (max - min));
//            donatur.setVaNumber(vaNumber);
//        }

        // Simpan donatur baru ke database
        donaturRepository.save(donatur);

        return donatur;
    }

    @Override
    public JwtResponse authenticateGoogleUser(String accessToken) throws Exception {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        JsonNode userInfo = restTemplate.getForObject(url, JsonNode.class);

        if (userInfo == null || userInfo.has("error")) {
            throw new Exception("Invalid Google Access Token");
        }

        // Ambil data dari Google OAuth2 token
        String email = userInfo.path("email").asText(null);
        String username = userInfo.path("name").asText(null);
        String picture = userInfo.path("picture").asText(null);

        if (email == null) {
            throw new Exception("Email not found in Google token info");
        }

        // Temukan atau buat user baru
        Donatur donatur = donaturRepository.findByEmail(email)
                .orElseGet(() -> {
                    Donatur newDonatur = new Donatur();
                    newDonatur.setUsername(username);
                    newDonatur.setEmail(email);
                    newDonatur.setImage(picture);
                    newDonatur.setCreatedAt(new Date());
                    return donaturRepository.save(newDonatur);
                });

        // Konversi Donatur menjadi UserDetailsImpl
        UserDetailsImpl userDetails = UserDetailsImpl.build(donatur);

        // Buat Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Simpan Authentication di SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Hasilkan JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Kembalikan JWT token dan username
        return new JwtResponse(username, jwt);
    }




    @Override
    public ResponseMessage resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // Mencari donatur berdasarkan email
        Donatur existingDonatur = donaturRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Akun dengan email ini tidak ditemukan"));

        // Generate password random
        String randomPassword = generateRandomPassword(6);

        // Hash password random dan simpan ke database
        existingDonatur.setPassword(encoder.encode(randomPassword));
        donaturRepository.save(existingDonatur);

        // Kirim email dengan password random ke pengguna
        emailSenderService.sendEmailResetPassword(existingDonatur.getEmail(), "Reset Password",
                "Password baru Anda adalah: " + randomPassword);

        return new ResponseMessage(true, "Password berhasil direset. Silakan cek email Anda untuk password baru.");
    }

    @Override
    public ResponseMessage resetPasswordAdmin(ResetPasswordRequest resetPasswordRequest) {
        // Mencari donatur berdasarkan email
        Admin existingAdmin = adminRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Akun dengan email ini tidak ditemukan"));

        // Generate password random
        String randomPassword = generateRandomPassword(10);

        // Hash password random dan simpan ke database
        existingAdmin.setPassword(encoder.encode(randomPassword));
        adminRepository.save(existingAdmin);

        // Kirim email dengan password random ke pengguna
        emailSenderService.sendEmailResetPassword(existingAdmin.getEmail(), "LAZIS Sultan Agung: Reset Password",
                "Password baru Anda adalah: " + randomPassword);

        return new ResponseMessage(true, "Password berhasil direset. Silakan cek email Anda untuk password baru.");
    }

    // Fungsi untuk generate password acak
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }

}
