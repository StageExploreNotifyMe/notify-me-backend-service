package be.xplore.notify.me.dto.mappers.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.mappers.DtoMapper;
import be.xplore.notify.me.dto.mappers.OrganizationDtoMapper;
import be.xplore.notify.me.dto.mappers.UserDtoMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventLineDtoMapper implements DtoMapper<EventLineDto, EventLine> {
    private final LineDtoMapper lineDtoMapper;
    private final EventDtoMapper eventDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final OrganizationDtoMapper organizationDtoMapper;

    public EventLineDtoMapper(LineDtoMapper lineDtoMapper, EventDtoMapper eventDtoMapper, UserDtoMapper userDtoMapper, OrganizationDtoMapper organizationDtoMapper) {
        this.lineDtoMapper = lineDtoMapper;
        this.eventDtoMapper = eventDtoMapper;
        this.userDtoMapper = userDtoMapper;
        this.organizationDtoMapper = organizationDtoMapper;
    }

    @Override
    public EventLine fromDto(EventLineDto d) {
        return EventLine.builder()
                .id(d.getId())
                .line(lineDtoMapper.fromDto(d.getLine()))
                .organization(organizationDtoMapper.fromDto(d.getOrganization()))
                .event(eventDtoMapper.fromDto(d.getEvent()))
                .assignedUsers(d.getAssignedUsers().stream().map(userDtoMapper::fromDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    public EventLineDto toDto(EventLine d) {
        return new EventLineDto(
                d.getId(),
                lineDtoMapper.toDto(d.getLine()),
                eventDtoMapper.toDto(d.getEvent()),
                organizationDtoMapper.toDto(d.getOrganization()),
                d.getAssignedUsers().stream().map(userDtoMapper::toDto).collect(Collectors.toList())
        );
    }
}
