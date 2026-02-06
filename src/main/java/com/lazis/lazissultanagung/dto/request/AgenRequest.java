package com.lazis.lazissultanagung.dto.request;

import lombok.Data;

@Data
public class AgenRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private String address;
}
