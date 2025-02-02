package com.lazis.lazissultanagung.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile multipartFile) {
        try {
            // Upload file ke Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
                    ObjectUtils.emptyMap());

            // Mengembalikan URL gambar yang di-upload
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void delete(String imageUrl) {
        try {
            // Ekstrak public ID dari URL
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Gagal menghapus gambar dari Cloudinary", e);
        }
    }

    // Metode untuk mengekstrak public ID dari URL
    private String extractPublicId(String imageUrl) {
        // Contoh: URL https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
    }

}

