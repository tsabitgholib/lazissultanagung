package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Literatur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiteraturRepository extends JpaRepository<Literatur, Integer> {

    List<Literatur> findByLiteraturNameContainingIgnoreCase(String literaturName);
}
