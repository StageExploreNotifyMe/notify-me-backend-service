package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.persistence.EventRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class EventServiceTest {
    @Autowired
    private EventService eventService;
    @MockBean
    private EventRepo eventRepo;
    @MockBean
    private EventNotificationService eventNotificationService;

    @Autowired
    private Venue venue;
    @Autowired
    private Event event;

    @Test
    void createEvent() {
        mockSave();
        LocalDateTime eventTime = LocalDateTime.now().plusMonths(2);
        String eventName = "Test Event 1";
        Event event = eventService.createEvent(eventTime, eventName, venue);
        assertEquals(eventName, event.getName());
        assertEquals(eventTime, event.getDate());
        assertEquals(venue.getId(), event.getVenue().getId());
    }

    @Test
    void createEventInThePast() {
        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(LocalDateTime.now().minusMonths(5), "test", venue));
    }

    @Test
    void getEventsOfVenue() {
        mockGetEventsByVenueId();
        Page<Event> eventsOfVenue = eventService.getEventsOfVenue(event.getVenue().getId(), 0);
        List<Event> events = eventsOfVenue.getContent();
        assertEquals(1, events.size());
        assertEquals(event.getId(), events.get(0).getId());
    }

    @Test
    void cancelEvent() {
        mockSave();
        Event updatedEvent = eventService.cancelEvent(this.event);
        assertEquals(EventStatus.CANCELED, updatedEvent.getEventStatus());
        assertEquals(event.getId(), updatedEvent.getId());
    }

    @Test
    void publishEvent() {
        mockSave();
        Event updatedEvent = eventService.publishEvent(this.event);
        assertEquals(EventStatus.PUBLIC, updatedEvent.getEventStatus());
        assertEquals(event.getId(), updatedEvent.getId());
    }

    private void mockGetEventsByVenueId() {
        given(eventRepo.getEventsOfVenue(any(), any())).will(i -> {
            List<Event> entityList = new ArrayList<>();
            if (i.getArgument(0).equals(event.getVenue().getId())) {
                entityList.add(event);
            }
            return new PageImpl<>(entityList);
        });
    }

    private void mockSave() {
        given(eventRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void getById() {
        mockFindById();
        Optional<Event> eventLineOptional = eventService.getById(event.getId());
        assertTrue(eventLineOptional.isPresent());
        assertEquals(event.getId(), eventLineOptional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        mockFindById();
        Optional<Event> eventLineOptional = eventService.getById("qdsf");
        assertTrue(eventLineOptional.isEmpty());
    }

    private void mockFindById() {
        given(eventRepo.findById(any())).will(i -> i.getArgument(0).equals(event.getId()) ? Optional.of(event) : Optional.empty());
    }
}