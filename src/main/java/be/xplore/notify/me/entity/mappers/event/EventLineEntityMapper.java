package be.xplore.notify.me.entity.mappers.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventLineEntityMapper implements EntityMapper<EventLineEntity, EventLine> {

    private final OrganizationEntityMapper organizationEntityMapper;
    private final EventEntityMapper eventEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final LineEntityMapper lineEntityMapper;

    public EventLineEntityMapper(
            OrganizationEntityMapper organizationEntityMapper,
            EventEntityMapper eventEntityMapper,
            UserEntityMapper userEntityMapper,
            LineEntityMapper lineEntityMapper
    ) {
        this.organizationEntityMapper = organizationEntityMapper;
        this.eventEntityMapper = eventEntityMapper;
        this.userEntityMapper = userEntityMapper;
        this.lineEntityMapper = lineEntityMapper;
    }

    @Override
    public EventLine fromEntity(EventLineEntity eventLineEntity) {
        return EventLine.builder()
                .id(eventLineEntity.getId())
                .event(eventEntityMapper.fromEntity(eventLineEntity.getEvent()))
                .organization(organizationEntityMapper.fromEntity(eventLineEntity.getOrganization()))
                .assignedUsers(eventLineEntity.getAssignedUsers().stream().map(userEntityMapper::fromEntity).collect(Collectors.toList()))
                .line(lineEntityMapper.fromEntity(eventLineEntity.getLine()))
                .eventLineStatus(eventLineEntity.getEventLineStatus())
                .lineManager(userEntityMapper.fromEntity(eventLineEntity.getLineManager()))
                .build();
    }

    @Override
    public EventLineEntity toEntity(EventLine eventLine) {
        return new EventLineEntity(
        eventLine.getId(),
        lineEntityMapper.toEntity(eventLine.getLine()),
        eventEntityMapper.toEntity(eventLine.getEvent()),
        eventLine.getEventLineStatus(),
        organizationEntityMapper.toEntity(eventLine.getOrganization()),
        eventLine.getAssignedUsers().stream().map(userEntityMapper::toEntity).collect(Collectors.toList()),
        userEntityMapper.toEntity(eventLine.getLineManager())
        );
    }
}
