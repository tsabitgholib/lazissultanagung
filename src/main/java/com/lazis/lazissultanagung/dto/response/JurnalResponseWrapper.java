package com.lazis.lazissultanagung.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class JurnalResponseWrapper {
    private List<JurnalResponse> jurnalResponses; // Daftar jurnal yang akan dikirim
    private Double totalDebitKeseluruhan;                    // Total debit seluruh jurnal
    private Double totalKreditKeseluruhan;                   // Total kredit seluruh jurnal
}
