package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Wakaf;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WakafRepository extends JpaRepository<Wakaf, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Wakaf w SET w.amount = w.amount + :transactionAmount WHERE w.id = :id")
    void updateWakafCurrentAmount(@Param("id") Long id, @Param("transactionAmount") double transactionAmount);

    @Transactional
    @Modifying
    @Query("UPDATE Wakaf w SET w.distribution = w.distribution + :distributionAmount WHERE w.id = :id")
    void updateWakafDistribution(@Param("id") Long id, @Param("distributionAmount") double distributionAmount);

    Page<Wakaf> findAll(Pageable pageable);

    @Query("SELECT COALESCE(SUM(w.amount), 0.0) FROM Wakaf w")
    double getTotalWakafAmount();


}
