package com.lazis.lazissultanagung.repository;

import com.lazis.lazissultanagung.model.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {

    @Query("SELECT m FROM Messages m ORDER BY messagesDate DESC")
    Page<Messages> getAllCampaignMessages(Pageable pageable);
}
