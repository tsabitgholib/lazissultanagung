package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.service.AgenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/agen")
public class AgenController {

    @Autowired
    private AgenService agenService;

    @PostMapping("/create")
    public ResponseEntity<Agen> createAgen(@RequestBody AgenRequest agenRequest) {
        Agen savedAgen = agenService.createAgen(agenRequest);
        return ResponseEntity.ok(savedAgen);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Agen>> getAllAgen() {
        return ResponseEntity.ok(agenService.getAllAgen());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Agen> getAgenById(@PathVariable Long id) {
        return ResponseEntity.ok(agenService.getAgenById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Agen> updateAgen(@PathVariable Long id,
                                           @RequestBody AgenRequest agenRequest) {
        Agen updatedAgen = agenService.updateAgen(id, agenRequest);
        return ResponseEntity.ok(updatedAgen);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAgen(@PathVariable Long id) {
        agenService.deleteAgen(id);
        return ResponseEntity.noContent().build();
    }
}
