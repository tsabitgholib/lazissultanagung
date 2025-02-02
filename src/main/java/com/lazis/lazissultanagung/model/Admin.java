package com.lazis.lazissultanagung.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lazis.lazissultanagung.enumeration.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    @Size(min = 12, max = 13)
    private String phoneNumber;

    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private String image;

    private String address;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    private long vaNumber;

    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole role;

    public Admin(String username, String phoneNumber,  String email, String password, String address, ERole role) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.createdAt = new Date();
        this.address = address;
        this.role = role;
    }
}