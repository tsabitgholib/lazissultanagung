package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.ZiswafImageRequest;
import com.lazis.lazissultanagung.model.ZiswafImage;
import com.lazis.lazissultanagung.service.ZiswafImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/ziswaf-image")
public class ZiswafImageController {

    @Autowired
    private ZiswafImageService service;

    @PostMapping("/create")
    public ResponseEntity<ZiswafImage> create(@ModelAttribute ZiswafImageRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ZiswafImage>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ZiswafImage> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ZiswafImage> update(@PathVariable Long id,
                                                @ModelAttribute ZiswafImageRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-by-category/{category}")
    public ResponseEntity<List<ZiswafImage>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

}

