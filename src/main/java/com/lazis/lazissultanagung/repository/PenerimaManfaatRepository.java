package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.PenerimaManfaat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenerimaManfaatRepository extends JpaRepository<PenerimaManfaat, Long> {
}
