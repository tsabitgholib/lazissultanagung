package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.NomorBuktiSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface NomorBuktiSequenceRepository extends JpaRepository<NomorBuktiSequence, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<NomorBuktiSequence> findByPeriod(String period);
}
