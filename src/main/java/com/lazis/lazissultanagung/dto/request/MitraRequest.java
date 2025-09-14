package com.lazis.lazissultanagung.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MitraRequest {
    private int id;
    private String name;
    private MultipartFile image;
}
