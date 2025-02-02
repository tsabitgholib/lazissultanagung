package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.model.Messages;
import com.lazis.lazissultanagung.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController
@RequestMapping("api/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    @GetMapping
    public ResponseEntity<Page<Messages>> getAllCampaignMessages(@RequestParam(name = "page", defaultValue = "0") int page){
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        Page<Messages> messages = messagesService.getAllCampaignMessages(pageRequest);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{id}/aamiin")
    public ResponseEntity<String> incrementAamiin(@PathVariable("id") Long id) {
        messagesService.incrementAamiin(id);
        return ResponseEntity.ok("Terimakasih Sudah mengAamiinkan doa ini");
    }

    @PostMapping("/{id}/batal-aamiin")
    public ResponseEntity<String> deincrementAamiin(@PathVariable("id") Long id) {
        messagesService.deincrementAamiin(id);
        return ResponseEntity.ok("Aamiin dibatalkan");
    }
}
