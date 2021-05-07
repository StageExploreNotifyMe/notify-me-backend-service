package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.mappers.event.EventEntityMapper;
import be.xplore.notify.me.repositories.EventRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class EventService {

    private final EventRepo eventRepo;
    private final EventEntityMapper eventEntityMapper;
    private final EventNotificationService eventNotificationService;

    public EventService(EventRepo eventRepo, EventEntityMapper eventEntityMapper, EventNotificationService notificationService) {
        this.eventRepo = eventRepo;
        this.eventEntityMapper = eventEntityMapper;
        this.eventNotificationService = notificationService;
    }

    public Event createEvent(LocalDateTime dateTime, String name, Venue venue) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            log.trace("Tried to create event " + name + " in the past: " + dateTime);
            throw new IllegalArgumentException("Event cannot be created in the past");
        }

        Event event = save(Event.builder().date(dateTime).venue(venue).eventStatus(EventStatus.CREATED).name(name).build());
        eventNotificationService.eventCreated(event);
        return event;
    }

    public Page<Event> getEventsOfVenue(String venueId, int page) {
        Page<EventEntity> eventEntityPage = eventRepo.getAllByVenue_IdOrderByDate(venueId, PageRequest.of(page, 20));
        return eventEntityPage.map(eventEntityMapper::fromEntity);
    }

    public Optional<Event> getById(String id) {
        Optional<EventEntity> optional = eventRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Event event = eventEntityMapper.fromEntity(optional.get());
        return Optional.of(event);
    }

    public Event save(Event event) {
        EventEntity eventEntity = eventRepo.save(eventEntityMapper.toEntity(event));
        return eventEntityMapper.fromEntity(eventEntity);
    }

    public Event cancelEvent(Event event) {
        Event toSave = updateEventStatus(event, EventStatus.CANCELED);
        eventNotificationService.sendEventCanceledNotification(toSave);
        return save(toSave);
    }

    public Event publishEvent(Event event) {
        Event toSave = updateEventStatus(event, EventStatus.PUBLIC);
        return save(toSave);
    }

    private Event updateEventStatus(Event event, EventStatus status) {
        return Event.builder()
            .id(event.getId())
            .name(event.getName())
            .venue(event.getVenue())
            .date(event.getDate())
            .eventStatus(status)
            .build();
    }
}
