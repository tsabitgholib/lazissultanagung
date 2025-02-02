package com.lazis.lazissultanagung.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DashboardImageRequest {

    private MultipartFile image_1;
    private MultipartFile image_2;
    private MultipartFile image_3;
}
