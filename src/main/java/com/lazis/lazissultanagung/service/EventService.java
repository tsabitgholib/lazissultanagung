package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.EventRequest;
import com.lazis.lazissultanagung.model.Event;

import java.util.List;

public interface EventService {
    List<Event> getAllEvent();
    Event createEvent(EventRequest eventRequest);
    Event getEventById(Long id);
    Event updateEvent(Long id, EventRequest eventRequest);
    void deleteEvent(Long id);
}
