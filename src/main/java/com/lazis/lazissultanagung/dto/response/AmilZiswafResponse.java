package com.lazis.lazissultanagung.dto.response;

import lombok.Data;

@Data
public class AmilZiswafResponse {
    private Long id;              // ID dari entri
    private String type;          // Jenis entri (zakat, infak, dsb.)
    private String categoryName;   // Nama kategori
    private Double amount;         // Jumlah donasi
    private Double amil;           // Jumlah amil

    // Constructor
    public AmilZiswafResponse(Long id, String type, String categoryName, Double amount, Double amil) {
        this.id = id;
        this.type = type;
        this.categoryName = categoryName;
        this.amount = amount;
        this.amil = amil;
    }
}
