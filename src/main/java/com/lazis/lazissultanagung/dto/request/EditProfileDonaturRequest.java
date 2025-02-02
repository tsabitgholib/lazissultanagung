package com.lazis.lazissultanagung.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditProfileDonaturRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private MultipartFile image;
    private String address;
}
