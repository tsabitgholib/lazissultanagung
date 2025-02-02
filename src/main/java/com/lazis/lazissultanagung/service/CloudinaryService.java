package com.lazis.lazissultanagung.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String upload(MultipartFile multipartFile);

    void delete(String imageUrl);
}
