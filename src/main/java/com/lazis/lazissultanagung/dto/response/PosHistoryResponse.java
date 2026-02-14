package com.lazis.lazissultanagung.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosHistoryResponse {
    private Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private LocalDateTime tanggal;
    
    private String nomorBukti;
    private String nama;
    private String noHp;
    private String email;
    private String alamat;
    private String kategori;
    private String subKategori;
    private Double nominal;
    private String metodePembayaran;
    private String paymentProofImage;
    private String namaEvent;
    private String lokasiEvent;
}
