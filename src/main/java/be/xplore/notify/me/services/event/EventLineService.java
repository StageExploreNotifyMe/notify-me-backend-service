package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.mappers.event.EventLineEntityMapper;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.repositories.EventLineRepo;
import be.xplore.notify.me.services.OrganizationNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventLineService {
    private final EventLineRepo eventLineRepo;
    private final EventLineEntityMapper eventLineEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final OrganizationNotificationService organizationNotificationService;
    private final EventLineNotificationService eventLineNotificationService;

    public EventLineService(
            EventLineRepo eventLineRepo,
            EventLineEntityMapper eventLineEntityMapper,
            UserEntityMapper userEntityMapper,
            OrganizationNotificationService organizationNotificationService,
            EventLineNotificationService eventLineNotificationService) {
        this.eventLineRepo = eventLineRepo;
        this.eventLineEntityMapper = eventLineEntityMapper;
        this.userEntityMapper = userEntityMapper;
        this.organizationNotificationService = organizationNotificationService;
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
        organizationNotificationService.sendOrganizationLineAssignmentNotification(organization, updatedLine);
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
        List<User> assignedUsers = line.getAssignedUsers();
        if (assignedUsers.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return line;
        }

        assignedUsers.add(user);
        EventLine saved = save(updateAssignedUsers(line, assignedUsers));
        eventLineNotificationService.notifyLineAssigned(user, saved);
        return saved;
    }

    public Page<EventLine> getAllLinesOfOrganization(String id, int pageNumber) {
        Page<EventLineEntity> eventLineEntityPage = eventLineRepo.getAllByOrganization_IdOrderByEvent_date(id, PageRequest.of(pageNumber, 20));
        return eventLineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    public Page<EventLine> getAllLinesOfUser(User user, int pageNumber) {
        Page<EventLineEntity> eventLineEntityPage = eventLineRepo.getAllByAssignedUsersContainsOrderByEvent_date(userEntityMapper.toEntity(user), PageRequest.of(pageNumber, 20));
        return eventLineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    public EventLine cancelUserEventLine(String userId, EventLine line) {
        List<User> assignedUsers = line.getAssignedUsers();
        Optional<User> userOptional = assignedUsers.stream().filter(u -> u.getId().equals(userId)).findAny();
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " is not assigned to this line.");
        }

        assignedUsers.remove(userOptional.get());
        eventLineNotificationService.sendMemberCanceledNotification(userId, line);
        return save(updateAssignedUsers(line, assignedUsers));
    }

    private EventLine updateAssignedUsers(EventLine line, List<User> users) {
        return EventLine.builder()
            .id(line.getId())
            .line(line.getLine())
            .event(line.getEvent())
            .assignedUsers(users)
            .organization(line.getOrganization())
            .build();
    }
}
