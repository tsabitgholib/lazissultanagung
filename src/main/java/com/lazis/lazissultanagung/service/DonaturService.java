package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.EditProfileDonaturRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Donatur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonaturService {

    Donatur getCurrentDonatur() throws com.lazis.lazissultanagung.exception.BadRequestException;

    Donatur editProfileDonatur(EditProfileDonaturRequest editProfileRequest) throws BadRequestException;

    Page<Donatur> getAllDonatur(Pageable pageable);

    Page<Donatur> searchDonaturs(String search, Pageable pageable);
}
