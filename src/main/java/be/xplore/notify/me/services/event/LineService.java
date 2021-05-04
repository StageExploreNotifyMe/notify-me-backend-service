package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.entity.event.LineEntity;
import be.xplore.notify.me.entity.mappers.event.LineEntityMapper;
import be.xplore.notify.me.repositories.LineRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LineService {

    private final LineRepo lineRepo;
    private final LineEntityMapper lineEntityMapper;

    public LineService(LineRepo lineRepo, LineEntityMapper lineEntityMapper) {
        this.lineRepo = lineRepo;
        this.lineEntityMapper = lineEntityMapper;
    }

    public Page<Line> getAllByVenue(String venueId, int page) {
        Page<LineEntity> lineEntityPage = lineRepo.getAllByVenueEntity_IdOrderByName(venueId, PageRequest.of(page, 20));
        return lineEntityPage.map(lineEntityMapper::fromEntity);
    }

    public Optional<Line> getById(String id) {
        Optional<LineEntity> optional = lineRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Line d = lineEntityMapper.fromEntity(optional.get());
        return Optional.of(d);
    }

    public Line save(Line line) {
        LineEntity lineEntity = lineRepo.save(lineEntityMapper.toEntity(line));
        return lineEntityMapper.fromEntity(lineEntity);
    }
}
