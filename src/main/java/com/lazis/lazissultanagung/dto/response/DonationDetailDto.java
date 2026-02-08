package com.lazis.lazissultanagung.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationDetailDto {
    private String kategori;
    private String subKategori;
    private Double nominal;
}
