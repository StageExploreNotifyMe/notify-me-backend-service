package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.mappers.event.EventEntityMapper;
import be.xplore.notify.me.repositories.EventRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EventService {
    private final EventRepo eventRepo;
    private final EventEntityMapper eventEntityMapper;

    public EventService(EventRepo eventRepo, EventEntityMapper eventEntityMapper) {
        this.eventRepo = eventRepo;
        this.eventEntityMapper = eventEntityMapper;
    }

    public Event createEvent(LocalDateTime dateTime, String name, Venue venue) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            log.trace("Tried to create event " + name + " in the past: " + dateTime);
            throw new IllegalArgumentException("Event cannot be created in the past");
        }

        Event event = Event.builder().date(dateTime).venue(venue).eventStatus(EventStatus.CREATED).name(name).build();
        return save(event);
    }

    public Page<Event> getEventsOfVenue(String venueId, int page) {
        try {
            Page<EventEntity> eventEntityPage = eventRepo.getAllByVenue_IdOrderByDate(venueId, PageRequest.of(page, 20));
            return eventEntityPage.map(eventEntityMapper::fromEntity);
        } catch (Exception e) {
            log.error("Failed to fetch events for venue {}: {}: {}", venueId, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public Event save(Event event) {
        try {
            EventEntity save = eventRepo.save(eventEntityMapper.toEntity(event));
            return eventEntityMapper.fromEntity(save);
        } catch (Exception e) {
            log.error("Failed to save event with id {}: {}: {}", event.getId(), e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }
}
