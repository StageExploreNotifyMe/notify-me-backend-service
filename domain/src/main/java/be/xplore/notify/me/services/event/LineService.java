package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.persistence.LineRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LineService {

    private final LineRepo lineRepo;

    public LineService(LineRepo lineRepo) {
        this.lineRepo = lineRepo;
    }

    public Page<Line> getAllByVenue(String venueId, int page) {
        return lineRepo.getAllByVenue(venueId, PageRequest.of(page, 20));
    }

    public Optional<Line> getById(String id) {
        return lineRepo.findById(id);
    }

    public Line save(Line line) {
        return lineRepo.save(line);

    }

    public Line createLine(Line line) {
        return save(line);
    }
}
