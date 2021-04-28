package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.mappers.event.EventEntityMapper;
import be.xplore.notify.me.repositories.EventRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class EventServiceTest {
    @Autowired
    private EventEntityMapper eventEntityMapper;
    @Autowired
    private EventService eventService;
    @MockBean
    private EventRepo eventRepo;

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
    void saveDbException() {
        given(eventRepo.save(any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> eventService.save(Event.builder().build()));
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
    void getEventsOfVenueDbException() {
        given(eventRepo.getAllByVenue_IdOrderByDate(any(), any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> eventService.getEventsOfVenue(event.getVenue().getId(), 0));
    }

    private void mockGetEventsByVenueId() {
        given(eventRepo.getAllByVenue_IdOrderByDate(any(), any())).will(i -> {
            List<EventEntity> entityList = new ArrayList<>();
            if (i.getArgument(0).equals(event.getVenue().getId())) {
                entityList.add(eventEntityMapper.toEntity(event));
            }
            return new PageImpl<>(entityList);
        });
    }

    private void mockSave() {
        given(eventRepo.save(any())).will(i -> i.getArgument(0));
    }
}