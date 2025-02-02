package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.NewsTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsTopicRepository extends JpaRepository<NewsTopic, Long> {
}
