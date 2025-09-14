package com.lazis.lazissultanagung.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Literatur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String literaturName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;
}
