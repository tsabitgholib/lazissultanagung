package com.lazis.lazissultanagung.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JurnalUmumRequest {
    private LocalDate transactionDate; // Tanggal transaksi
    private String description; // Deskripsi transaksi
    private String categoryType; // Jenis kategori (wakaf, zakat, campaign, dll.)
    private Long categoryId; // ID kategori yang terkait dengan transaksi
    private boolean penyaluran;
    private List<DebitDetail> debitDetails; // Daftar detail debet
    private List<KreditDetail> kreditDetails; // Daftar detail kredit

    // Inner class untuk Debit Detail
    @Data
    public static class DebitDetail {
        private Long coaId; // ID COA untuk debet
        private Double amount; // Nominal yang didebet
    }

    // Inner class untuk Kredit Detail
    @Data
    public static class KreditDetail {
        private Long coaId; // ID COA untuk kredit
        private Double amount; // Nominal yang dikreditkan
    }
}
