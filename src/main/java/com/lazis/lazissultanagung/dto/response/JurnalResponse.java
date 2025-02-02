package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JurnalResponse {
    private LocalDate tanggal;
    private String unit;
    private String nomorBukti;
    private String uraian;
    private int jumlahDebit;
    private int jumlahKredit;
    private List<COAResponse> coa; // Daftar COA
    private double totalDebit;
    private double totalKredit;
}
