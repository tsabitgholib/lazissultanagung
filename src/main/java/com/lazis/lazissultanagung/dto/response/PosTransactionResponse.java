package com.lazis.lazissultanagung.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosTransactionResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggal;
    
    private String nama;
    private String noHp;
    private String email;
    private String alamat;
    
    private List<DonationDetailDto> donasi;
    
    private String terbilang;
}
