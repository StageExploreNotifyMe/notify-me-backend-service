package be.xplore.notify.me.util.mockadapters.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.persistence.LineRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LineAdapter implements LineRepo {

    @Override
    public Page<Line> getAllByVenue(String venueId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Optional<Line> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Line save(Line line) {
        return null;
    }
}
