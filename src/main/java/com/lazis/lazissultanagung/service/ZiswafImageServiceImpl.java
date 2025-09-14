package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.ZiswafImageRequest;
import com.lazis.lazissultanagung.model.ZiswafImage;
import com.lazis.lazissultanagung.repository.ZiswafImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZiswafImageServiceImpl implements ZiswafImageService{

    @Autowired
    private ZiswafImageRepository ziswafImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private final String baseUrl = "https://skyconnect.lazis-sa.org/api/images/";

    private String buildImageUrl(String imageName) {
        return (imageName != null && !imageName.isEmpty())
                ? baseUrl + imageName
                : null;
    }

    @Override
    public ZiswafImage create(ZiswafImageRequest request) {
        String imageFile = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageFile = fileStorageService.saveFile(request.getImage());
        }

        ZiswafImage entity = new ZiswafImage();
        entity.setCategory(request.getCategory());
        entity.setImage(imageFile);

        ZiswafImage saved = ziswafImageRepository.save(entity);
        saved.setImage(buildImageUrl(saved.getImage()));
        return saved;
    }

    @Override
    public List<ZiswafImage> getAll() {
        return ziswafImageRepository.findAll().stream()
                .peek(e -> e.setImage(buildImageUrl(e.getImage())))
                .toList();
    }

    @Override
    public ZiswafImage getById(Long id) {
        ZiswafImage entity = ziswafImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ZiswafImage not found with id " + id));
        entity.setImage(buildImageUrl(entity.getImage()));
        return entity;
    }

    @Override
    public ZiswafImage update(Long id, ZiswafImageRequest request) {
        ZiswafImage entity = getById(id);

        if (request.getCategory() != null) {
            entity.setCategory(request.getCategory());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageFile = fileStorageService.saveFile(request.getImage());
            entity.setImage(imageFile);
        }

        ZiswafImage updated = ziswafImageRepository.save(entity);
        updated.setImage(buildImageUrl(updated.getImage()));
        return updated;
    }

    @Override
    public void delete(Long id) {
        ZiswafImage entity = getById(id);
        ziswafImageRepository.delete(entity);
    }

    @Override
    public List<ZiswafImage> getByCategory(String category) {
        return ziswafImageRepository.findByCategoryIgnoreCase(category).stream()
                .peek(e -> e.setImage(buildImageUrl(e.getImage())))
                .toList();
    }
}
