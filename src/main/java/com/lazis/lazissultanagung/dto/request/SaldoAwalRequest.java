package com.lazis.lazissultanagung.dto.request;

import lombok.Data;

@Data
public class SaldoAwalRequest {
    private Long coaId;
    private Double debit;
    private Double kredit;
}
