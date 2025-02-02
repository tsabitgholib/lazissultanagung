package com.lazis.lazissultanagung.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "saldo_awal")
public class SaldoAwal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coa_id", referencedColumnName = "id")
    private Coa coa;

    @Column(nullable = false)
    private double saldoAwal;

    @Column(nullable = false)
    private double debit;

    @Column(nullable = false)
    private double kredit;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalInput;

    // Getters dan Setters
}
