package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo {
    Page<Event> getEventsOfVenue(String venueId, PageRequest pageRequest);

    Optional<Event> findById(String id);

    Event save(Event event);

    Page<Event> getAllEventsBetween(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, PageRequest pageRequest);

    List<Event> findAllByIds(List<String> ids);
}
