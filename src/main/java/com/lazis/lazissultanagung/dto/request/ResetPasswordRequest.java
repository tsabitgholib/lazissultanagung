package com.lazis.lazissultanagung.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class ResetPasswordRequest {

    @Email(message = "Format email tidak valid")
    @NotEmpty(message = "Email tidak boleh kosong")
    private String email;
}
