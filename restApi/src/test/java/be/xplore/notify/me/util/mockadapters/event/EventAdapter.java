package be.xplore.notify.me.util.mockadapters.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.persistence.EventRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventAdapter implements EventRepo {

    @Override
    public Page<Event> getEventsOfVenue(String venueId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Optional<Event> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Event save(Event event) {
        return null;
    }
}
