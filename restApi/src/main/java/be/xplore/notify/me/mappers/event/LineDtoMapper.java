package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.dto.event.LineDto;
import be.xplore.notify.me.mappers.DtoMapper;
import be.xplore.notify.me.mappers.VenueDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LineDtoMapper implements DtoMapper<LineDto, Line> {
    private final VenueDtoMapper venueDtoMapper;

    public LineDtoMapper(VenueDtoMapper venueDtoMapper) {
        this.venueDtoMapper = venueDtoMapper;
    }

    @Override
    public Line fromDto(LineDto d) {
        return Line.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .venue(venueDtoMapper.fromDto(d.getVenueDto()))
                .numberOfRequiredPeople(d.getNumberOfRequiredPeople())
                .build();
    }

    @Override
    public LineDto toDto(Line d) {
        return new LineDto(
                d.getId(),
                d.getName(),
                d.getDescription(),
                venueDtoMapper.toDto(d.getVenue()),
                d.getNumberOfRequiredPeople()
        );
    }
}
