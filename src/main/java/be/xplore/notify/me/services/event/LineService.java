package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.event.LineEntity;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.repositories.LineRepo;
import be.xplore.notify.me.services.RepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LineService extends RepoService<Line, LineEntity> {

    private final LineRepo lineRepo;

    public LineService(LineRepo repo, EntityMapper<LineEntity, Line> entityMapper) {
        super(repo, entityMapper);
        this.lineRepo = repo;
    }

    public Page<Line> getAllByVenue(String venueId, int page) {
        try {
            Page<LineEntity> lineEntityPage = lineRepo.getAllByVenueEntity_IdOrderByName(venueId, PageRequest.of(page, 20));
            return lineEntityPage.map(entityMapper::fromEntity);
        } catch (Exception e) {
            log.error("Failed to fetch lines for venue {}: {}: {}", venueId, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }
}
