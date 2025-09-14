package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.ZiswafImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZiswafImageRepository extends JpaRepository<ZiswafImage, Long> {
    List<ZiswafImage> findByCategoryIgnoreCase(String category);
}
