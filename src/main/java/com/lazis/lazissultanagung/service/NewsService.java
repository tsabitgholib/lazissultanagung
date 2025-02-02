package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.NewsRequest;
import com.lazis.lazissultanagung.dto.response.NewsResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.News;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface NewsService {

    Page<NewsResponse> getAllNews(Pageable pageable);

    Optional<NewsResponse> getNewsById(Long id);

    NewsResponse createNews(NewsRequest newsRequest);

    NewsResponse updateNews(Long id, NewsRequest newsRequest);

    ResponseMessage deleteNews(Long id);

    @Transactional
    ResponseMessage approveNews(Long id) throws BadRequestException;

    Page<NewsResponse> getNewsByTitleAndTopic(String title, String newsTopic, Pageable pageable);
}
