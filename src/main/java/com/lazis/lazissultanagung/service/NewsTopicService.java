package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.NewsTopic;

import java.util.List;

public interface NewsTopicService {
    List<NewsTopic> getAllTopic();

    NewsTopic createTopic(NewsTopic newsTopic);

    NewsTopic updateTopic(Long id, NewsTopic newsTopic);

    ResponseMessage deleteTopic(Long id);
}
