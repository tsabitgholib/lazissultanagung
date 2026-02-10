package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.AgenRequest;
import com.lazis.lazissultanagung.dto.response.AgenResponse;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.service.AgenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping("/create")
    public ResponseEntity<?> createAgen(@Valid @RequestBody AgenRequest agenRequest) {
        return ResponseEntity.ok(agenService.createAgen(agenRequest));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateAgen(@PathVariable Long id, @RequestBody AgenRequest agenRequest) {
        return ResponseEntity.ok(agenService.updateAgen(id, agenRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAgen(@PathVariable Long id) {
        agenService.deleteAgen(id);
        return ResponseEntity.ok("Agen deleted successfully");
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getAgenById(@PathVariable Long id) {
        return ResponseEntity.ok(agenService.getAgenById(id));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<AgenResponse>> getAllAgen() {
        return ResponseEntity.ok(agenService.getAllAgen());
    }
}
