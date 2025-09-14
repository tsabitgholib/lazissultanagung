package com.lazis.lazissultanagung.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PenerimaManfaat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int jumlahPenerimaManfaat;
    private int penerimaManfaatCampaign;
    private int penerimaManfaatZakat;
    private int penerimaManfaatInfak;
    private int penerimaManfaatWakaf;
    private int penerimaManfaatDSKL;
}
