package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {

    private String username;
    private String token;
    private String type = "Bearer";

    public JwtResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
