package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.entity.event.LineEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.mappers.VenueEntityMapper;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

@Component
public class LineEntityMapper implements EntityMapper<LineEntity, Line> {

    private final VenueEntityMapper venueEntityMapper;

    public LineEntityMapper(VenueEntityMapper venueEntityMapper) {
        this.venueEntityMapper = venueEntityMapper;
    }

    @Override
    public Line fromEntity(LineEntity lineEntity) {
        if (lineEntity == null) {
            return null;
        }
        return Line.builder()
                .id(String.valueOf(lineEntity.getId()))
                .name(lineEntity.getName())
                .description(lineEntity.getDescription())
                .venue(venueEntityMapper.fromEntity(lineEntity.getVenueEntity()))
                .numberOfRequiredPeople(lineEntity.getNumberOfRequiredPeople())
                .build();
    }

    @Override
    public LineEntity toEntity(Line line) {
        if (line == null) {
            return null;
        }
        return new LineEntity(
                LongParser.parseLong(line.getId()),
                line.getName(),
                line.getDescription(),
                venueEntityMapper.toEntity(line.getVenue()),
                line.getNumberOfRequiredPeople()
        );
    }
}
