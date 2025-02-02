package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessagesService {
    Page<Messages> getAllCampaignMessages(Pageable pageable);

    void incrementAamiin(Long id);

    void deincrementAamiin(Long id);
}
