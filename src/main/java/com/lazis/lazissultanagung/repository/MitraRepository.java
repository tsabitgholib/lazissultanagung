package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Mitra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MitraRepository extends JpaRepository<Mitra, Long> {
}
