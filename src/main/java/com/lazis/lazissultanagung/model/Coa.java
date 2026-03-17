package com.lazis.lazissultanagung.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private LocalDateTime deletedAt;

    public enum AccountType {
        Asset, Liability, Equity, Revenue, Expense
    }
}

