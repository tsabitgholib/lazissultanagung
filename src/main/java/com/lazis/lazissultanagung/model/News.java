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
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String newsImage;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private NewsTopic newsTopic;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "creator",referencedColumnName = "id")
    private Admin admin;

    @Column(columnDefinition = "BOOLEAN")
    private boolean approved;
}