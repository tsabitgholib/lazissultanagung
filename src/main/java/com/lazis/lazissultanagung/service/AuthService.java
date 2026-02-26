package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.ResetPasswordRequest;
import com.lazis.lazissultanagung.dto.response.JwtResponse;
import com.lazis.lazissultanagung.dto.request.SignInRequest;
import com.lazis.lazissultanagung.dto.request.SignUpRequest;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Donatur;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    JwtResponse authenticateUser(SignInRequest signinRequest, HttpServletResponse response, String userType) throws BadRequestException;

    Admin registerAdmin(SignUpRequest signUpRequest) throws BadRequestException;

    Donatur registerDonatur(SignUpRequest signUpRequest) throws BadRequestException;

    JwtResponse authenticateGoogleUser(String accessToken) throws Exception;

    ResponseMessage resetPassword(ResetPasswordRequest resetPasswordRequest);

    ResponseMessage resetPasswordAdmin(ResetPasswordRequest resetPasswordRequest);
}
