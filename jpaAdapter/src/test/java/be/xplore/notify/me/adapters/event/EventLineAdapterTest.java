package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class EventLineAdapterTest {

    @Autowired
    private EventLineAdapter eventLineAdapter;

    @Test
    void getLineManagersByEvent() {
        List<User> lineManagersByEvent = eventLineAdapter.getLineManagersByEvent(Event.builder().id("1").build());
        assertFalse(lineManagersByEvent.isEmpty());
    }

    @Test
    void findById() {
        Optional<EventLine> byId = eventLineAdapter.findById("1");
        assertTrue(byId.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<EventLine> byId = eventLineAdapter.findById("500");
        assertTrue(byId.isEmpty());
    }

    @Test
    void save() {
        EventLine eventLine = EventLine.builder().id("501").assignedUsers(new ArrayList<>()).build();
        EventLine save = eventLineAdapter.save(eventLine);
        assertEquals(eventLine.getAssignedUsers().size(), save.getAssignedUsers().size());
    }

    @Test
    void getAllLinesOfOrganization() {
        Page<EventLine> page = eventLineAdapter.getAllLinesOfOrganization("1", PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void getAllLinesOfUser() {
        Page<EventLine> page = eventLineAdapter.getAllLinesOfUser(User.builder().id("1").build(), PageRequest.of(0, 20));
        assertTrue(page.isEmpty());
    }

    @Test
    void getAllLinesOfEvent() {
        Page<EventLine> page = eventLineAdapter.getAllLinesOfEvent("1", PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void getAllActiveEventLinesOfLineManager() {
        List<EventLine> lines = eventLineAdapter.getAllActiveEventLinesOfLineManager("1");
        assertEquals("1", lines.get(0).getLineManager().getId());
    }
}