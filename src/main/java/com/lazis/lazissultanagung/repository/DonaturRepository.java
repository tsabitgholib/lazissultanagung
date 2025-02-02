package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Donatur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonaturRepository extends JpaRepository<Donatur, Long> {
    Optional<Donatur> findByPhoneNumber(String phoneNumber);
    Optional<Donatur> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    @Query("SELECT d FROM Donatur d")
    Page<Donatur> getAllDonatur(Pageable pageable);

    @Query("SELECT d FROM Donatur d WHERE " +
            "LOWER(d.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Donatur> search(@Param("search") String search, Pageable pageable);
}