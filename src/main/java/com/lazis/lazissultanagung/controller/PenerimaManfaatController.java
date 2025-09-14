package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.PenerimaManfaat;
import com.lazis.lazissultanagung.service.PenerimaManfaatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penerima-manfaat")
public class PenerimaManfaatController {

    @Autowired
    private PenerimaManfaatService penerimaManfaatService;

    @GetMapping
    public ResponseEntity<List<PenerimaManfaat>> getAll() {
        return ResponseEntity.ok(penerimaManfaatService.getAllPenerimaManfaat());
    }

    @GetMapping("/get/one")
    public ResponseEntity<PenerimaManfaat> getByIdOne() {
        return penerimaManfaatService.getByIdOne()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<PenerimaManfaat> add(@RequestBody PenerimaManfaat penerimaManfaat) {
        return ResponseEntity.ok(penerimaManfaatService.addPenerimaManfaat(penerimaManfaat));
    }

    @PutMapping("/edit/1")
    public ResponseEntity<PenerimaManfaat> editByIdOne(@RequestBody PenerimaManfaat penerimaManfaat) {
        return ResponseEntity.ok(penerimaManfaatService.editPenerimaManfaatIdOne(penerimaManfaat));
    }
}
