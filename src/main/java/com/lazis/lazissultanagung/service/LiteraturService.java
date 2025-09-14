package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.Literatur;
import com.lazis.lazissultanagung.repository.LiteraturRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LiteraturService {

    private final LiteraturRepository literaturRepository;

    public LiteraturService(LiteraturRepository literaturRepository) {
        this.literaturRepository = literaturRepository;
    }

    public List<Literatur> getAll() {
        return literaturRepository.findAll();
    }

    public Optional<Literatur> getById(int id) {
        return literaturRepository.findById(id);
    }

    public Literatur save(Literatur literatur) {
        return literaturRepository.save(literatur);
    }

    public Literatur update(int id, Literatur literatur) {
        return literaturRepository.findById(id)
                .map(existing -> {
                    existing.setLiteraturName(literatur.getLiteraturName());
                    existing.setText(literatur.getText());
                    return literaturRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Literatur not found with id " + id));
    }

    public void delete(int id) {
        literaturRepository.deleteById(id);
    }

    public List<Literatur> getByLiteraturName(String name) {
        return literaturRepository.findByLiteraturNameContainingIgnoreCase(name);
    }
}
