package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.SignInRequest;
import com.lazis.lazissultanagung.service.AgenAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api/auth/agen")
public class AgenAuthController {

    @Autowired
    private AgenAuthService agenAuthService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateAgen(@Valid @RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok(agenAuthService.authenticateAgen(signInRequest));
    }
}
