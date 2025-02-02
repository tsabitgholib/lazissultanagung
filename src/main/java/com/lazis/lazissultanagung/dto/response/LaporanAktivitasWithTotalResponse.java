package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LaporanAktivitasWithTotalResponse {
    private List<LaporanAktivitasResponse> aktivitas;
    private Double finalTotalMonth1;
    private Double finalTtotalMonth2;
}
