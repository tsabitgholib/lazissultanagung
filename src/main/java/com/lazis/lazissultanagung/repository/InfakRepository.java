package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Infak;
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
public interface InfakRepository extends JpaRepository<Infak, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Infak i SET i.amount = i.amount + :transactionAmount WHERE i.id = :id")
    void updateInfakCurrentAmount(@Param("id") Long id, @Param("transactionAmount") double transactionAmount);

    @Transactional
    @Modifying
    @Query("UPDATE Infak i SET i.distribution = i.distribution + :distributionAmount WHERE i.id = :id")
    void updateInfakDistribution(@Param("id") Long id, @Param("distributionAmount") double distributionAmount);

    Page<Infak> findAll(Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.amount), 0.0) FROM Infak i")
    double getTotalInfakAmount();

    @Query("SELECT i.categoryName, i.amount FROM Infak i")
    List<Object[]> findAllPenghimpunan();

}
