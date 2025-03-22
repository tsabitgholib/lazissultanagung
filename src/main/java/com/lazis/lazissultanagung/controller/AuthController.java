package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.GoogleAccessTokenRequest;
import com.lazis.lazissultanagung.dto.request.ResetPasswordRequest;
import com.lazis.lazissultanagung.dto.response.JwtResponse;
import com.lazis.lazissultanagung.dto.request.SignInRequest;
import com.lazis.lazissultanagung.dto.request.SignUpRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Donatur;
import com.lazis.lazissultanagung.service.AuthService;

import com.lazis.lazissultanagung.service.DonaturService;
import com.lazis.lazissultanagung.service.EmailSenderService;
import com.lazis.lazissultanagung.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    EmailSenderService emailSenderService;

    @PostMapping("/signin/donatur")
    public ResponseEntity<?> authenticateDonatur(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateUser(signInRequest, response, "DONATUR"));
    }



    @PostMapping("/signin/admin")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateUser(signInRequest, response, "ADMIN"));
    }


    @PostMapping("/signup/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody SignUpRequest signUpRequest) {
        Admin admin = authService.registerAdmin(signUpRequest);
        return ResponseEntity.ok(admin);
    }

    @PostMapping("/signup/donatur")
    public ResponseEntity<?> registerDonatur(@RequestBody SignUpRequest signUpRequest) {
        Donatur donatur = authService.registerDonatur(signUpRequest);

//        String toEmail = signUpRequest.getEmail();
//        String subject = "Laporan Donasi Anda";
//        String body = "Bismillah Bisaa ini ngirim email";
//        emailSenderService.sendRegisterReport(toEmail, subject, body);

        return ResponseEntity.ok(donatur);
    }

    @PostMapping("/google")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody GoogleAccessTokenRequest tokenRequest) {
        try {
            // ✅ Logging untuk cek apakah token masuk ke backend
            System.out.println("Received Access Token: " + tokenRequest.getAccess_token());

            String accessToken = tokenRequest.getAccess_token();
            JwtResponse authResponse = authService.authenticateGoogleUser(accessToken);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            System.out.println("Google Authentication Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponse("Unauthorized", null));
        }
    }



    @PostMapping("/reset-password")
    public ResponseEntity<ResponseMessage> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        ResponseMessage responseMessage = authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/reset-password-admin")
    public ResponseEntity<ResponseMessage> resetPasswordAdmin(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        ResponseMessage responseMessage = authService.resetPasswordAdmin(resetPasswordRequest);
        return ResponseEntity.ok(responseMessage);
    }

}
