package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.EditProfileDonaturRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdminService {
    Admin getCurrentAdmin() throws BadRequestException;

    Page<Map<String, Object>> getAllOperator(Pageable pageable);

    Admin editProfileAdmin(EditProfileDonaturRequest editProfileRequest) throws BadRequestException;

    ResponseMessage nonaktiveOperator(Long id);

    ResponseMessage aktiveOperator(Long id);

    Page<Map<String, Object>> getActiveOperator(Pageable pageable);

    Page<Admin> searchOperators(String search, Pageable pageable);

    Page<Map<String, Object>> getAllKeuangan(Pageable pageable);
}
