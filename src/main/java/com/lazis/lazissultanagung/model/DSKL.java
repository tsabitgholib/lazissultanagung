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
public class DSKL implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String categoryName;

    @org.hibernate.annotations.Formula("(SELECT COALESCE(SUM(t.debit), 0) FROM transaction t WHERE t.dskl_id = id AND t.penyaluran = 0 AND t.success = 1)")
    private double amount;

    @org.hibernate.annotations.Formula("(SELECT COALESCE(SUM(d.distribution_amount), 0) FROM distribution d WHERE d.dskl_id = id AND d.success = 1)")
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
