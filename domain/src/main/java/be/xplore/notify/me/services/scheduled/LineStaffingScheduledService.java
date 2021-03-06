package be.xplore.notify.me.services.scheduled;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class LineStaffingScheduledService {

    private final EventService eventService;
    private final EventLineService eventLineService;
    private final NotificationService notificationService;

    public LineStaffingScheduledService(
            EventService eventService,
            EventLineService eventLineService,
            NotificationService notificationService
    ) {
        this.eventService = eventService;
        this.eventLineService = eventLineService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${notify.me.scheduled.staffing.incomplete.cron:0 4 * * * ?}")
    public void checkLineStaffing() {
        boolean hasNext;
        int page = 0;
        do {
            Page<Event> upcomingEvents = eventService.getUpcomingEvents(2, page);
            page++;
            hasNext = upcomingEvents.hasNext();
            upcomingEvents.forEach(this::checkEventStaffing);
        } while (hasNext);
    }

    private void checkEventStaffing(Event event) {
        boolean hasNext;
        int page = 0;
        do {
            Page<EventLine> linesOfEvent = eventLineService.getAllLinesOfEvent(event.getId(), page);
            page++;
            hasNext = linesOfEvent.hasNext();
            linesOfEvent.get().forEach(this::checkEventLineStaffing);
        } while (hasNext);
    }

    private void checkEventLineStaffing(EventLine eventLine) {
        switch (eventLine.getEventLineStatus()) {
            case CANCELED:
                break;
            case CREATED:
                sendNoOrgAssignNotification(eventLine);
                break;
            case ASSIGNED:
                sendStaffingIncompleteNotification(eventLine);
                break;
            default:
                log.warn("Unknown eventline status in line staffing scheduled service: " + eventLine.getEventLineStatus());
        }
    }

    private void sendStaffingIncompleteNotification(EventLine eventLine) {
        if (eventLine.getAssignedUsers().size() >= eventLine.getLine().getNumberOfRequiredPeople()) {
            return;
        }
        User user = eventLine.getLineManager();
        Event event = eventLine.getEvent();
        String body = String.format(
                "Hi %s %s\n\nEvent %s has a line %s with incomplete staffing: %s/%s.  Assigned organization is %s.  Event is due to start at %s on %s",
                user.getFirstname(),
                user.getLastname(),
                event.getName(),
                eventLine.getLine().getName(),
                eventLine.getAssignedUsers().size(),
                eventLine.getLine().getNumberOfRequiredPeople(),
                eventLine.getOrganization().getName(),
                event.getDate().toLocalTime(),
                event.getDate().toLocalDate()
        );
        Notification notification = Notification.builder()
                .type(NotificationType.STAFFING_REMINDER)
                .urgency(NotificationUrgency.URGENT)
                .title("Line with incomplete staffing")
                .creationDate(LocalDateTime.now())
                .userId(user.getId())
                .eventId(event.getId())
                .usedChannel(user.getUserPreferences().getUrgentChannel())
                .body(body)
                .build();

        notificationService.sendNotification(notification, user);
    }

    private void sendNoOrgAssignNotification(EventLine eventLine) {
        User user = eventLine.getLineManager();
        Event event = eventLine.getEvent();
        String body = String.format(
                "Hi %s %s\n\nEvent %s has a line %s without an organization assigned. Event is due to start at %s on %s",
                user.getFirstname(),
                user.getLastname(),
                event.getName(),
                eventLine.getLine().getName(),
                event.getDate().toLocalTime(),
                event.getDate().toLocalDate()
        );
        Notification notification = Notification.builder()
                .type(NotificationType.STAFFING_REMINDER)
                .urgency(NotificationUrgency.URGENT)
                .title("Line without an organization assigned")
                .creationDate(LocalDateTime.now())
                .userId(user.getId())
                .eventId(event.getId())
                .usedChannel(user.getUserPreferences().getUrgentChannel())
                .body(body)
                .build();

        notificationService.sendNotification(notification, user);
    }

}
