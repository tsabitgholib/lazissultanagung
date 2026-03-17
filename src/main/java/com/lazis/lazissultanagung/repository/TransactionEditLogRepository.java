package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.TransactionEditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionEditLogRepository extends JpaRepository<TransactionEditLog, Long> {
    List<TransactionEditLog> findByNomorBuktiOrderByEditTimeDesc(String nomorBukti);
    Page<TransactionEditLog> findAllByOrderByEditTimeDesc(Pageable pageable);
}
