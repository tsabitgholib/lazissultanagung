package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.response.ResponseMessage;
import com.lazis.lazissultanagung.model.NewsTopic;
import com.lazis.lazissultanagung.service.NewsTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/newsTopic")
public class NewsTopicController {

    @Autowired
    private NewsTopicService newsTopicService;

    @GetMapping
    public ResponseEntity<List<NewsTopic>> getAllTopic(){
        List<NewsTopic> newsTopics = newsTopicService.getAllTopic();
        return ResponseEntity.ok().body(newsTopics);
    }

    @PostMapping("/create")
    public ResponseEntity<NewsTopic> createTopic(@RequestBody NewsTopic newsTopic){
        NewsTopic createNews = newsTopicService.createTopic(newsTopic);
        return ResponseEntity.ok().body(createNews);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NewsTopic> updateTopic(@PathVariable Long id,
                                                 @RequestBody NewsTopic newsTopic){
        NewsTopic updateTopic = newsTopicService.updateTopic(id, newsTopic);
        return ResponseEntity.ok().body(updateTopic);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteTopic(@PathVariable Long id){
        ResponseMessage response = newsTopicService.deleteTopic(id);
        return ResponseEntity.ok().body(response);
    }
}
