package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.EventLineRepo;
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
    private final EventLineNotificationService eventLineNotificationService;
    private final OrganizationNotificationService organizationNotificationService;

    public EventLineService(
            EventLineRepo eventLineRepo,
            EventLineNotificationService eventLineNotificationService,
            OrganizationNotificationService organizationNotificationService
    ) {
        this.eventLineRepo = eventLineRepo;
        this.eventLineNotificationService = eventLineNotificationService;
        this.organizationNotificationService = organizationNotificationService;
    }

    public Page<EventLine> getAllLinesOfEvent(String eventId, int page) {
        return eventLineRepo.getAllLinesOfEvent(eventId, PageRequest.of(page, 20));
    }

    public EventLine addLineToEvent(Line line, Event event, User lineManager) {
        return save(EventLine.builder()
            .event(event)
            .line(line)
            .assignedUsers(new ArrayList<>())
            .eventLineStatus(EventLineStatus.CREATED)
            .lineManager(lineManager)
            .eventLineStatus(EventLineStatus.CREATED)
            .build());
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
        organizationNotificationService.sendOrganizationLineAssignmentNotification(organization, updatedLine);
        return save(updatedLine);
    }

    public List<User> getLineManagersByEvent(Event event) {
        return eventLineRepo.getLineManagersByEvent(event);
    }

    public Optional<EventLine> getById(String id) {
        return eventLineRepo.findById(id);
    }

    public EventLine save(EventLine eventLine) {
        return eventLineRepo.save(eventLine);
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
        return eventLineRepo.getAllLinesOfOrganization(id, PageRequest.of(pageNumber, 20));
    }

    public EventLine cancelEventLine(EventLine eventLine) {
        EventLine toSave = updateEventLineStatus(eventLine, EventLineStatus.CANCELED);
        eventLineNotificationService.sendEventLineCanceledNotification(toSave);
        return save(toSave);
    }

    private EventLine updateEventLineStatus(EventLine eventLine, EventLineStatus status) {
        return EventLine.builder()
            .id(eventLine.getId())
            .line(eventLine.getLine())
            .organization(eventLine.getOrganization())
            .lineManager(eventLine.getLineManager())
            .assignedUsers(eventLine.getAssignedUsers())
            .eventLineStatus(status)
            .event(eventLine.getEvent())
            .build();
    }

    public Page<EventLine> getAllLinesOfUser(User user, int pageNumber) {
        return eventLineRepo.getAllLinesOfUser(user, PageRequest.of(pageNumber, 20));
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
            .eventLineStatus(line.getEventLineStatus())
            .lineManager(line.getLineManager())
            .build();
    }
}
