package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Agen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgenRepository extends JpaRepository<Agen, Long> {
    Optional<Agen> findByEmail(String email);
    Optional<Agen> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
