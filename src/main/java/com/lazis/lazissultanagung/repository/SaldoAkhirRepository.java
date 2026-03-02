package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.model.SaldoAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaldoAkhirRepository extends JpaRepository<SaldoAkhir, Long> {
    Optional<SaldoAkhir> findByCoaAndMonthAndYear(Coa coa, int month, int year);
    Optional<SaldoAkhir> findByCoa_IdAndMonthAndYear(Long coaId, int month, int year);

    @Query("SELECT COALESCE(SUM(sa.saldoAkhir), 0.0) FROM SaldoAkhir sa WHERE sa.coa.id = :coaId")
    Optional<Double> findSaldoAkhirByCoaId(@Param("coaId") Long coaId);
}
