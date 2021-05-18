package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.persistence.EventRepo;
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
    private final EventNotificationService eventNotificationService;

    public EventService(EventRepo eventRepo, EventNotificationService eventNotificationService) {
        this.eventRepo = eventRepo;
        this.eventNotificationService = eventNotificationService;
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
        return eventRepo.getEventsOfVenue(venueId, PageRequest.of(page, 20));

    }

    public Optional<Event> getById(String id) {
        return eventRepo.findById(id);
    }

    public Event save(Event event) {
        return eventRepo.save(event);
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

    public Page<Event> getUpcomingEvents(int numberOfDays, int page) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(numberOfDays);
        return eventRepo.getAllEventsBetween(now, endDate, PageRequest.of(page, 20));
    }
}
