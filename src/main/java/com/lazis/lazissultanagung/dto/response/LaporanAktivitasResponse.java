package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

@Data
public class LaporanAktivitasResponse {
    private String accountCode;
    private String accountName;
    private String parentAccountCode;
    private String parentAccountName;
    private Double totalMonth1;
    private Double totalMonth2;
}
