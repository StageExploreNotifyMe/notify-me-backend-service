package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.mappers.event.EventLineEntityMapper;
import be.xplore.notify.me.repositories.EventLineRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class EventLineService {
    private final EventLineRepo eventLineRepo;
    private final EventLineEntityMapper eventLineEntityMapper;
    private final EventLineNotificationService eventLineNotificationService;

    public EventLineService(EventLineRepo eventLineRepo, EventLineEntityMapper eventLineEntityMapper, EventLineNotificationService eventLineNotificationService) {
        this.eventLineRepo = eventLineRepo;
        this.eventLineEntityMapper = eventLineEntityMapper;
        this.eventLineNotificationService = eventLineNotificationService;
    }

    public Page<EventLine> getAllLinesOfEvent(String eventId, int page) {

        Page<EventLineEntity> lineEntityPage = eventLineRepo.getAllByEvent_IdOrderByLine(eventId, PageRequest.of(page, 20));
        return lineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    public EventLine addLineToEvent(Line line, Event event) {
        return save(EventLine.builder().event(event).line(line).assignedUsers(new ArrayList<>()).build());
    }

    public EventLine assignOrganizationToLine(Organization organization, EventLine line) {
        EventLine updatedLine = EventLine.builder()
                .id(line.getId())
                .line(line.getLine())
                .organization(organization)
                .event(line.getEvent())
                .assignedUsers(new ArrayList<>())
                .build();
        return save(updatedLine);
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
        EventLine saved = save(line);
        eventLineNotificationService.notifyLineAssigned(user, line);
        return saved;
    }

    public Page<EventLine> getAllLinesOfOrganization(String id, int pageNumber) {
        Page<EventLineEntity> eventLineEntityPage = eventLineRepo.getAllByOrganization_IdOrderByEvent_date(id, PageRequest.of(pageNumber, 20));
        return eventLineEntityPage.map(eventLineEntityMapper::fromEntity);
    }
}
