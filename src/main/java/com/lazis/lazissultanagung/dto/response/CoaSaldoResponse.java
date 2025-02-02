package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

@Data
public class CoaSaldoResponse {
    private Long coaId;
    private String accountCode;
    private String accountName;
    private String parentAccountName;
    private double debit;
    private double kredit;
    private double saldoAwal;
}
