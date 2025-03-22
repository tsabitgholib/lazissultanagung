package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.News;
import com.lazis.lazissultanagung.model.NewsTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE n.approved = true ORDER BY n.date DESC")
    Page<News> findAllApprovedNews(Pageable pageable);

    @Query("SELECT n FROM News n WHERE " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%')) OR :title IS NULL) " +
            "AND (n.newsTopic.newsTopic = :newsTopic OR :newsTopic IS NULL) " +
            "AND n.approved = true ORDER BY n.date DESC")
    Page<News> findByTitleAndTopic(@Param("title") String title,
                                   @Param("newsTopic") String newsTopic,
                                   Pageable pageable);

    boolean existsByNewsTopic(NewsTopic newsTopic);

}
