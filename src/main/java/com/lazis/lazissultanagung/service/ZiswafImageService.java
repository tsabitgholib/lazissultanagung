package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.ZiswafImageRequest;
import com.lazis.lazissultanagung.model.ZiswafImage;

import java.util.List;

public interface ZiswafImageService {
    ZiswafImage create(ZiswafImageRequest request);

    List<ZiswafImage> getAll();

    ZiswafImage getById(Long id);

    ZiswafImage update(Long id, ZiswafImageRequest request);

    void delete(Long id);

    List<ZiswafImage> getByCategory(String category);
}
