package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BukuBesarResponse {
    private LocalDate tanggal;
    private String unit;
    private String nomorBukti;
    private String uraian;
    private double debit;
    private double kredit;
    private double saldo;

}
