package com.lazis.lazissultanagung.model;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code", length = 10, nullable = false)
    private String accountCode;

    @Column(name = "account_name", length = 100, nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "parent_account_id")
    @Nullable
    private Coa parentAccount;

    public enum AccountType {
        Asset, Liability, Equity, Revenue, Expense
    }
}

