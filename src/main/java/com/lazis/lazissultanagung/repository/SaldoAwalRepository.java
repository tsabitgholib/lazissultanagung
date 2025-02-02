package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Coa;
import com.lazis.lazissultanagung.model.SaldoAwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaldoAwalRepository extends JpaRepository<SaldoAwal, Long> {
    Optional<SaldoAwal> findByCoa(Coa coa);

    List<SaldoAwal> findAllByTanggalInputBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT sa.saldoAwal FROM SaldoAwal sa WHERE sa.coa.id = :coaId")
    Optional<Double> findByCoaId(@Param("coaId") Long coaId);

    @Query("SELECT COALESCE(SUM(sa.saldoAwal), 0.0) FROM SaldoAwal sa WHERE sa.coa.id = :coaId")
    Optional<Double> findSaldoAwalByCoaId(@Param("coaId") Long coaId);

    @Query("SELECT s FROM SaldoAwal s WHERE s.coa.id = :coaId AND MONTH(s.tanggalInput) = :month AND YEAR(s.tanggalInput) = :year")
    Optional<SaldoAwal> findSaldoAwalByCoaAndMonthAndYear(@Param("coaId") Long coaId, @Param("month") int month, @Param("year") int year);
}
