package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.NomZakat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NomZakatRepository extends JpaRepository<NomZakat, Long> {
}
