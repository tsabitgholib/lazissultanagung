package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.NewsRequest;
import com.lazis.lazissultanagung.dto.response.NewsResponse;
import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public Page<NewsResponse> getAllNews(@RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12; // Jumlah per halaman
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return newsService.getAllNews(pageRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable long id){
        Optional<NewsResponse> newsOptional = newsService.getNewsById(id);
        if (newsOptional.isPresent()){
            return new ResponseEntity<>(newsOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<NewsResponse> createNews(@ModelAttribute NewsRequest newsRequest){
        NewsResponse createdNews = newsService.createNews(newsRequest);
        return ResponseEntity.ok().body(createdNews);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable long id, @ModelAttribute NewsRequest newsRequest){
        NewsResponse updateNews = newsService.updateNews(id,newsRequest);
        return ResponseEntity.ok().body(updateNews);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteNews(@PathVariable long id) {
        return newsService.deleteNews(id);
    }

    @CrossOrigin
    @PutMapping("/approve-news/{id}")
    public ResponseMessage approveNews(@PathVariable Long id) {
        return newsService.approveNews(id);
    }

    @GetMapping("/search")
    public Page<NewsResponse> getNews(@RequestParam(required = false) String title,
                                      @RequestParam(required = false) String newsTopic,
                                      @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return newsService.getNewsByTitleAndTopic(title, newsTopic, pageRequest);
    }

}
