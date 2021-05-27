package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class EventAdapterTest {

    @Autowired
    private EventAdapter eventAdapter;

    @Test
    void getEventsOfVenue() {
        Page<Event> eventsOfVenue = eventAdapter.getEventsOfVenue("1", PageRequest.of(0, 20));
        assertTrue(eventsOfVenue.hasContent());
    }

    @Test
    void findById() {
        Optional<Event> byId = eventAdapter.findById("1");
        assertTrue(byId.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<Event> byId = eventAdapter.findById("qmfdqf");
        assertTrue(byId.isEmpty());
    }

    @Test
    void save() {
        Event event = Event.builder().name("test").build();
        Event save = eventAdapter.save(event);
        assertEquals(event.getName(), save.getName());
    }

    @Test
    void getAllEventsBetween() {
        LocalDateTime now = LocalDateTime.now();
        Page<Event> between = eventAdapter.getAllEventsBetween(now.minusDays(1), now.plusDays(1), PageRequest.of(0, 20));
        assertTrue(between.hasContent());
        Page<Event> none = eventAdapter.getAllEventsBetween(LocalDateTime.MIN, LocalDateTime.MIN.plusSeconds(1), PageRequest.of(0, 20));
        assertTrue(none.isEmpty());
    }
}