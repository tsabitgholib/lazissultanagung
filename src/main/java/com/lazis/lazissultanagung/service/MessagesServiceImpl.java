package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.Messages;
import com.lazis.lazissultanagung.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessagesServiceImpl implements MessagesService{

    @Autowired
    private MessagesRepository messagesRepository;

    @Override
    public Page<Messages> getAllCampaignMessages(Pageable pageable){
        return messagesRepository.getAllCampaignMessages(pageable);
    }

    @Override
    public void incrementAamiin(Long id) {
        Messages message = messagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        message.setAamiin(message.getAamiin() + 1);

        messagesRepository.save(message);
    }

    @Override
    public void deincrementAamiin(Long id) {
        Messages message = messagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        message.setAamiin(message.getAamiin() - 1);

        messagesRepository.save(message);
    }
}
