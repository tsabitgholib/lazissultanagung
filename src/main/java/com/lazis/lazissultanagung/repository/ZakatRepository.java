package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Zakat;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ZakatRepository extends JpaRepository<Zakat, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Zakat z SET z.amount = z.amount + :transactionAmount WHERE z.id = :id")
    void updateZakatCurrentAmount(@Param("id") Long id, @Param("transactionAmount") double transactionAmount);

    @Transactional
    @Modifying
    @Query("UPDATE Zakat z SET z.distribution = z.distribution + :distributionAmount WHERE z.id = :id")
    void updateZakatDistribution(@Param("id") Long id, @Param("distributionAmount") double distributionAmount);

    Page<Zakat> findAll(Pageable pageable);

    @Query("SELECT COALESCE(SUM(z.amount), 0.0) FROM Zakat z")
    double getTotalZakatAmount();

}
