package com.lazis.lazissultanagung.config;


import com.lazis.lazissultanagung.dto.response.BukuBesarResponse;
import lombok.Data;

import java.util.List;

@Data
public class BukuBesarWrapper {
    private double saldoAwal1;
    private List<BukuBesarResponse> bukuBesarCoa1;

    private double saldoAwal2;
    private List<BukuBesarResponse> bukuBesarCoa2;
}