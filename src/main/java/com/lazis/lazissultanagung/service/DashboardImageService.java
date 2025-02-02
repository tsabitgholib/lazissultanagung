package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.DashboardImageRequest;
import com.lazis.lazissultanagung.model.DashboardImage;

import java.util.List;

public interface DashboardImageService {
    List<DashboardImage> getAllDashboardImage();

    DashboardImage addDashboardImage(DashboardImageRequest dashboardImageRequest);

    DashboardImage editDashboardImage(Long id, DashboardImageRequest dashboardImageRequest);

    DashboardImage deleteDashboardImage(Long id, List<String> imagesToDelete);
}
