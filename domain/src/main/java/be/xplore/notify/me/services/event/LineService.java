package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
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

    public Optional<Line> findById(String id) {
        return lineRepo.findById(id);
    }

    public Line getById(String id) {
        Optional<Line> byId = findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new NotFoundException("No line found for id " + id);
    }

    public Line save(Line line) {
        return lineRepo.save(line);

    }

    public Line createLine(Line line) {
        return save(line);
    }

    public Line updateLine(Line fromDto) {
        Line original = lineById(fromDto.getId());
        Line newLine = Line.builder()
                .id(original.getId())
                .name(fromDto.getName())
                .description(fromDto.getDescription())
                .numberOfRequiredPeople(fromDto.getNumberOfRequiredPeople())
                .venue(original.getVenue())
                .build();
        return save(newLine);
    }

    private Line lineById(String id) {
        Optional<Line> optionalLine = findById(id);
        if (optionalLine.isEmpty()) {
            throw new NotFoundException("No line with id " + id + " found");
        }
        return optionalLine.get();
    }
}
