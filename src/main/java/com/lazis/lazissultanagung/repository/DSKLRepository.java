package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.DSKL;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DSKLRepository extends JpaRepository<DSKL, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE DSKL d SET d.amount = d.amount + :transactionAmount WHERE d.id = :id")
    void updateDSKLCurrentAmount(@Param("id") Long id, @Param("transactionAmount") double transactionAmount);

    @Transactional
    @Modifying
    @Query("UPDATE DSKL d SET d.distribution = d.distribution + :distributionAmount WHERE d.id = :id")
    void updateDSKLDistribution(@Param("id") Long id, @Param("distributionAmount") double distributionAmount);

    Page<DSKL> findAll(Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.amount), 0.0) FROM DSKL d")
    double getTotalDsklAmount();

    @Query("SELECT d.categoryName, d.amount FROM DSKL d")
    List<Object[]> findAllPenghimpunan();
}
