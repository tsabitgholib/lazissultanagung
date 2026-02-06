package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.dto.request.EventRequest;
import com.lazis.lazissultanagung.exception.BadRequestException;
import com.lazis.lazissultanagung.model.Event;
import com.lazis.lazissultanagung.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> getAllEvent() {
        return eventRepository.findAll();
    }

    @Override
    public Event createEvent(EventRequest eventRequest) {
        Event event = new Event();
        event.setName(eventRequest.getName());
        event.setLocation(eventRequest.getLocation());
        return eventRepository.save(event);
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Lokasi Event not found with id " + id));
    }

    @Override
    public Event updateEvent(Long id, EventRequest eventRequest) {
        Event existingEvent = getEventById(id);

        if (eventRequest.getName() != null) {
            existingEvent.setName(eventRequest.getName());
        }

        if (eventRequest.getLocation() != null) {
            existingEvent.setLocation(eventRequest.getLocation());
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }
}
