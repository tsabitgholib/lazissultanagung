package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.TemporaryTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporaryTransactionRepository extends JpaRepository<TemporaryTransaction, Long> {
    List<TemporaryTransaction> findByNomorBukti(String nomorBukti);
    List<TemporaryTransaction> findByAgenId(Long agenId);

    @Query(value = "SELECT t.nomor_bukti FROM temporary_transaction t WHERE t.nomor_bukti LIKE CONCAT('%/', :period) ORDER BY CAST(SUBSTRING_INDEX(t.nomor_bukti, '/', 1) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastNomorBuktiByPeriod(@Param("period") String period);
}
