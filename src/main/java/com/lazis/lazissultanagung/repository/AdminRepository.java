package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByPhoneNumber(String phoneNumber);
    Optional<Admin> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    @Query("SELECT a.id, a.username, a.email, a.phoneNumber, a.address, a.createdAt, a.active as status FROM Admin a WHERE a.role = 'OPERATOR' ORDER BY a.active DESC, a.id DESC")
    Page<Object[]> getAllOperator(Pageable pageable);

    @Query("SELECT a.username, a.email, a.phoneNumber, a.address, a.createdAt FROM Admin a WHERE a.role = 'OPERATOR' AND a.active = true ORDER BY a.id DESC")
    Page<Object[]> getActiveOperator(Pageable pageable);

    @Query("SELECT a FROM Admin a WHERE " +
            "(LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "a.role = 'OPERATOR'")
    Page<Admin> searchOperator(@Param("search") String search, Pageable pageable);

    @Query("SELECT a.id, a.username, a.email, a.phoneNumber, a.address, a.createdAt, a.active as status FROM Admin a WHERE a.role = 'KEUANGAN' ORDER BY a.active DESC, a.id DESC")
    Page<Object[]> getAllKeuangan(Pageable pageable);



}
