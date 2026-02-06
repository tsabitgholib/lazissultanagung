package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.service.AgenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/agen")
public class AgenController {

    @Autowired
    private AgenService agenService;

    @GetMapping("/get-me")
    public ResponseEntity<?> getCurrentUser() {
        Agen currentAgen = agenService.getCurrentAgen();
        return ResponseEntity.ok(currentAgen);
    }
}
