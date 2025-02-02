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
public class Wakaf implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String categoryName;
    private double amount;
    private double distribution;

    @Column(columnDefinition = "BOOLEAN")
    private boolean emergency;
}
