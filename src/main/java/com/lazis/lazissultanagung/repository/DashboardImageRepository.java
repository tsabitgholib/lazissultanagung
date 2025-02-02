package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.DashboardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardImageRepository extends JpaRepository<DashboardImage, Long> {

}
