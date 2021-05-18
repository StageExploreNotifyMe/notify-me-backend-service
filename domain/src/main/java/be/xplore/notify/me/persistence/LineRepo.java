package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.event.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface LineRepo {
    Page<Line> getAllByVenue(String venueId, PageRequest pageRequest);

    Optional<Line> findById(String id);

    Line save(Line line);
}
