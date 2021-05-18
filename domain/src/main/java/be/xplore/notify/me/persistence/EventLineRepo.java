package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface EventLineRepo {
    List<User> getLineManagersByEvent(Event event);

    Optional<EventLine> findById(String id);

    EventLine save(EventLine eventLine);

    Page<EventLine> getAllLinesOfOrganization(String id, PageRequest pageRequest);

    Page<EventLine> getAllLinesOfUser(User user, PageRequest pageRequest);

    Page<EventLine> getAllLinesOfEvent(String eventId, PageRequest pageRequest);
}
