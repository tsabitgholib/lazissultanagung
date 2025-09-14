package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.Literatur;
import com.lazis.lazissultanagung.service.LiteraturService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/literatur")
public class LiteraturController {

    private final LiteraturService literaturService;

    public LiteraturController(LiteraturService literaturService) {
        this.literaturService = literaturService;
    }

    @PostMapping("/create")
    public ResponseEntity<Literatur> create(@RequestBody Literatur literatur) {
        return ResponseEntity.ok(literaturService.save(literatur));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Literatur>> getAll() {
        return ResponseEntity.ok(literaturService.getAll());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Literatur> getById(@PathVariable int id) {
        return literaturService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Literatur> update(@PathVariable int id, @RequestBody Literatur literatur) {
        return ResponseEntity.ok(literaturService.update(id, literatur));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        literaturService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-by-literatur-name")
    public ResponseEntity<List<Literatur>> getByLiteraturName(@RequestParam String name) {
        return ResponseEntity.ok(literaturService.getByLiteraturName(name));
    }
}
