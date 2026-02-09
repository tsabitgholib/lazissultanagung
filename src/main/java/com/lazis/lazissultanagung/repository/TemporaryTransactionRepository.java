package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.TemporaryTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryTransactionRepository extends JpaRepository<TemporaryTransaction, Long> {
    List<TemporaryTransaction> findByNomorBukti(String nomorBukti);
}
