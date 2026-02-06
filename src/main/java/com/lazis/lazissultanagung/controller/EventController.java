package com.lazis.lazissultanagung.controller;

import com.lazis.lazissultanagung.dto.request.EventRequest;
import com.lazis.lazissultanagung.model.Event;
import com.lazis.lazissultanagung.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest eventRequest) {
        Event savedEvent = eventService.createEvent(eventRequest);
        return ResponseEntity.ok(savedEvent);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Event>> getAllEvent() {
        return ResponseEntity.ok(eventService.getAllEvent());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,
                                                         @RequestBody EventRequest eventRequest) {
        Event updatedEvent = eventService.updateEvent(id, eventRequest);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
