package com.lazis.lazissultanagung.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lazis.lazissultanagung.service.CloudinaryService;
import com.lazis.lazissultanagung.service.CloudinaryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary (ObjectUtils.asMap(
                "cloud_name", "lazissa",
                "api_key", "212128557543196",
                "api_secret", "U8nhoyx1PJe7YLGvOvg3ySMIdcQ"));
    }
    @Bean
    public CloudinaryService cloudinaryService(){
        return new CloudinaryServiceImpl(cloudinary());
    }

//            if (documentationRequest.getImage() != null && !documentationRequest.getImage().isEmpty()){
//        String imageUrl = cloudinaryService.upload(documentationRequest.getImage());
//        documentation.setImage(imageUrl);
//    }
}