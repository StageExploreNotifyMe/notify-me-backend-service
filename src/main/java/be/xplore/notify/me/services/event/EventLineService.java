package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.mappers.event.EventEntityMapper;
import be.xplore.notify.me.entity.mappers.event.EventLineEntityMapper;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.repositories.EventLineRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventLineService {
    private final EventLineRepo eventLineRepo;
    private final EventLineEntityMapper eventLineEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final EventEntityMapper eventEntityMapper;

    public EventLineService(EventLineRepo eventLineRepo, EventLineEntityMapper eventLineEntityMapper, UserEntityMapper userEntityMapper, EventEntityMapper eventEntityMapper) {
        this.eventLineRepo = eventLineRepo;
        this.eventLineEntityMapper = eventLineEntityMapper;
        this.userEntityMapper = userEntityMapper;
        this.eventEntityMapper = eventEntityMapper;
    }

    public Page<EventLine> getAllLinesOfEvent(String eventId, int page) {

        Page<EventLineEntity> lineEntityPage = eventLineRepo.getAllByEvent_IdOrderByLine(eventId, PageRequest.of(page, 20));
        return lineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    public EventLine addLineToEvent(Line line, Event event, User lineManager) {
        return save(EventLine.builder().event(event).line(line).assignedUsers(new ArrayList<>()).eventLineStatus(EventLineStatus.CREATED).lineManager(lineManager).build());
    }

    public EventLine assignOrganizationToLine(Organization organization, EventLine line) {
        EventLine updatedLine = EventLine.builder()
                .id(line.getId())
                .line(line.getLine())
                .organization(organization)
                .event(line.getEvent())
                .assignedUsers(new ArrayList<>())
                .lineManager(line.getLineManager())
                .eventLineStatus(EventLineStatus.ASSIGNED)
                .build();
        return save(updatedLine);
    }

    public List<User> getLineManagersByEvent(Event event) {
        List<EventLineEntity> eventLineEntity = eventLineRepo.getAllByEvent(eventEntityMapper.toEntity(event));
        List<UserEntity> lineManagersEntity = eventLineEntity.stream().map(EventLineEntity::getLineManager).collect(Collectors.toList());
        return lineManagersEntity.stream().map(userEntityMapper::fromEntity).collect(Collectors.toList());
    }

    public Optional<EventLine> getById(String id) {
        Optional<EventLineEntity> optional = eventLineRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        EventLine eventLine = eventLineEntityMapper.fromEntity(optional.get());
        return Optional.of(eventLine);
    }

    public EventLine save(EventLine eventLine) {
        EventLineEntity eventLineEntity = eventLineRepo.save(eventLineEntityMapper.toEntity(eventLine));
        return eventLineEntityMapper.fromEntity(eventLineEntity);
    }

    public EventLine assignUserToEventLine(User user, EventLine line) {
        if (line.getAssignedUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return line;
        }

        line.getAssignedUsers().add(user);
        return save(line);
    }

    public Page<EventLine> getAllLinesOfOrganization(String id, int pageNumber) {
        Page<EventLineEntity> eventLineEntityPage = eventLineRepo.getAllByOrganization_IdOrderByEvent_date(id, PageRequest.of(pageNumber, 20));
        return eventLineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    public EventLine cancelEventLine(EventLine eventLine) {
        EventLine toSave = updateEventLineStatus(eventLine, EventLineStatus.CANCELED);
        return save(toSave);
    }

    private EventLine updateEventLineStatus(EventLine eventLine, EventLineStatus status) {
        return EventLine.builder()
            .id(eventLine.getId())
            .line(eventLine.getLine())
            .organization(eventLine.getOrganization())
            .assignedUsers(eventLine.getAssignedUsers())
            .eventLineStatus(status)
            .event(eventLine.getEvent())
            .build();
    }
}
