package com.lazis.lazissultanagung.model;

import com.lazis.lazissultanagung.service.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Zakat implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String categoryName;
    private double amount;
    private double distribution;

    @Column(columnDefinition = "BOOLEAN")
    private boolean emergency;

    @ManyToOne
    @JoinColumn(name = "coa_debit_id", referencedColumnName = "id")
    private Coa coaDebit;

    @ManyToOne
    @JoinColumn(name = "coa_kredit_id", referencedColumnName = "id")
    private Coa coaKredit;
}
