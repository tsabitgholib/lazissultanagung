package com.lazis.lazissultanagung.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.lazis.lazissultanagung.model.Admin;
import com.lazis.lazissultanagung.model.Agen;
import com.lazis.lazissultanagung.model.Donatur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String phoneNumber;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(long id, String username, String phoneNumber, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Donatur donatur) {
        // Donatur tidak memiliki role, authority default bisa dikosongkan
        return new UserDetailsImpl(
                donatur.getId(),
                donatur.getUsername(),
                donatur.getPhoneNumber(),
                donatur.getEmail(),
                donatur.getPassword(),
                new HashSet<>()
        );
    }

    public static UserDetailsImpl build(Admin admin) {
        // Menambahkan role sesuai dengan admin
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(admin.getRole().name())); // role bisa 'admin' atau 'sub_admin'

        return new UserDetailsImpl(
                admin.getId(),
                admin.getUsername(),
                admin.getPhoneNumber(),
                admin.getEmail(),
                admin.getPassword(),
                authorities
        );
    }

    public static UserDetailsImpl build(Agen agen) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_AGEN"));

        return new UserDetailsImpl(
                agen.getId(),
                agen.getUsername(),
                agen.getPhoneNumber(),
                agen.getEmail(),
                agen.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return id == user.id;
    }
}

