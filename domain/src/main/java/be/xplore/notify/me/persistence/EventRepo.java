package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepo {
    Page<Event> getEventsOfVenue(String venueId, PageRequest pageRequest);

    Optional<Event> findById(String id);

    Event save(Event event);
}
