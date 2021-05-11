package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.entity.event.LineEntity;
import be.xplore.notify.me.mappers.event.LineEntityMapper;
import be.xplore.notify.me.repositories.JpaLineRepo;
import be.xplore.notify.me.persistence.LineRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LineAdapter implements LineRepo {

    private final JpaLineRepo jpaLineRepo;
    private final LineEntityMapper lineEntityMapper;

    public LineAdapter(JpaLineRepo jpaLineRepo, LineEntityMapper lineEntityMapper) {
        this.jpaLineRepo = jpaLineRepo;
        this.lineEntityMapper = lineEntityMapper;
    }

    @Override
    public Page<Line> getAllByVenue(String venueId, PageRequest pageRequest) {
        Page<LineEntity> lineEntityPage = jpaLineRepo.getAllByVenueEntity_IdOrderByName(venueId, pageRequest);
        return lineEntityPage.map(lineEntityMapper::fromEntity);
    }

    @Override
    public Optional<Line> findById(String id) {
        Optional<LineEntity> optional = jpaLineRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Line d = lineEntityMapper.fromEntity(optional.get());
        return Optional.of(d);
    }

    @Override
    public Line save(Line line) {
        LineEntity lineEntity = jpaLineRepo.save(lineEntityMapper.toEntity(line));
        return lineEntityMapper.fromEntity(lineEntity);
    }
}
