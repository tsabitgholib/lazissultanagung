package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.SignInRequest;
import com.lazis.lazissultanagung.dto.response.JwtResponse;
import com.lazis.lazissultanagung.exception.BadRequestException;

public interface AgenAuthService {
    JwtResponse authenticateAgen(SignInRequest signinRequest) throws BadRequestException;
}
