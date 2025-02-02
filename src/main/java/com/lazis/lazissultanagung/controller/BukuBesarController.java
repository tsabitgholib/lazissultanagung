package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.config.BukuBesarWrapper;
import com.lazis.lazissultanagung.dto.response.BukuBesarResponse;
import com.lazis.lazissultanagung.service.BukuBesarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("/api")
public class BukuBesarController {

    @Autowired
    private BukuBesarService bukuBesarService;

    @GetMapping("/buku-besar")
    public ResponseEntity<BukuBesarWrapper> getBukuBesar(
            @RequestParam Long coaId1,
            @RequestParam(required = false) Long coaId2,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(null);
        }

        BukuBesarWrapper response = bukuBesarService.getBukuBesar(coaId1, coaId2, startDate, endDate);
        return ResponseEntity.ok(response);
    }



}


