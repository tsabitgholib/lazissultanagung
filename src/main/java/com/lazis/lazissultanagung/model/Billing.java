package com.lazis.lazissultanagung.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long billingId;

    @ManyToOne
    @JoinColumn(name = "donatur_id", referencedColumnName = "id")
    private Donatur donatur;

    @Column(length = 20)
    private String username;

    @Column(length = 15)
    private String phoneNumber;

    @Column
    private String email;

    private double billingAmount;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s")
    private LocalDateTime billingDate;

    private String prefix;
    private String vaNumber;
    private String method;
    private String time;
    private String account;

    @Column(name = "transactionQrId")
    private long transactionQrId;
    private boolean success;

    @Column(length = 20)
    private String category;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "zakat_id")
    private Zakat zakat;

    @ManyToOne
    @JoinColumn(name = "infak_id")
    private Infak infak;

    @ManyToOne
    @JoinColumn(name = "dskl_id")
    private DSKL dskl;

    @ManyToOne
    @JoinColumn(name = "wakaf_id")
    private Wakaf wakaf;
}
