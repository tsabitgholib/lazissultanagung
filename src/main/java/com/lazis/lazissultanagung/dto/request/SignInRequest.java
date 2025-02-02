package com.lazis.lazissultanagung.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignInRequest {

    @NotBlank
    private String emailOrPhoneNumber;

    @NotBlank
    private String password;

}

