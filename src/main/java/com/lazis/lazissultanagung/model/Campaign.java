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
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private CampaignCategory campaignCategory;

    private String campaignName;

    private String campaignCode;

    private String campaignImage;

    private String campaignImageDesc1;

    private String campaignImageDesc2;

    private String campaignImageDesc3;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private double targetAmount;

    @org.hibernate.annotations.Formula("(SELECT COALESCE(SUM(t.debit), 0) FROM transaction t WHERE t.campaign_id = campaign_id AND t.penyaluran = 0 AND t.success = 1)")
    private double currentAmount;

    @ManyToOne
    @JoinColumn(name = "creator", referencedColumnName = "id")
    private Admin admin;

    @org.hibernate.annotations.Formula("(SELECT COALESCE(SUM(d.distribution_amount), 0) FROM distribution d WHERE d.campaign_id = campaign_id AND d.success = 1)")
    private double distribution;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(columnDefinition = "BOOLEAN")
    private boolean active;

    @Column(columnDefinition = "BOOLEAN")
    private boolean approved;

    @Column(columnDefinition = "BOOLEAN")
    private boolean emergency;

    @Column(columnDefinition = "BOOLEAN")
    private Boolean priority;
}

