package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.MitraRequest;
import com.lazis.lazissultanagung.model.Mitra;
import com.lazis.lazissultanagung.service.MitraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/mitra")
public class MitraController {

    @Autowired
    private MitraService mitraService;

    @PostMapping("/create")
    public ResponseEntity<Mitra> createMitra(@ModelAttribute MitraRequest mitraRequest) {
        Mitra savedMitra = mitraService.createMitra(mitraRequest);
        return ResponseEntity.ok(savedMitra);
    }

    // READ ALL
    @GetMapping("/get-all")
    public ResponseEntity<List<Mitra>> getAllMitra() {
        return ResponseEntity.ok(mitraService.getAllMitra());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Mitra> getMitraById(@PathVariable Long id) {
        return ResponseEntity.ok(mitraService.getMitraById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Mitra> updateMitra(@PathVariable Long id,
                                             @ModelAttribute MitraRequest mitraRequest) {
        Mitra updatedMitra = mitraService.updateMitra(id, mitraRequest);
        return ResponseEntity.ok(updatedMitra);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMitra(@PathVariable Long id) {
        mitraService.deleteMitra(id);
        return ResponseEntity.noContent().build();
    }
}
