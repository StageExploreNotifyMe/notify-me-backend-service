package be.xplore.notify.me.util.mockadapters.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.EventLineRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventLineAdapter implements EventLineRepo {

    @Override
    public List<User> getLineManagersByEvent(Event event) {
        return null;
    }

    @Override
    public Optional<EventLine> findById(String id) {
        return Optional.empty();
    }

    @Override
    public EventLine save(EventLine eventLine) {
        return null;
    }

    @Override
    public Page<EventLine> getAllLinesOfOrganization(String id, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Page<EventLine> getAllLinesOfUser(User user, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Page<EventLine> getAllLinesOfEvent(String eventId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public List<EventLine> getAllActiveEventLinesOfLineManager(String lineManagerId) {
        return null;
    }
}
