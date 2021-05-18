package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventNotificationService {
    private final NotificationSenderService notificationSenderService;
    private final NotificationService notificationService;
    private final EventLineService eventLineService;
    private final UserOrganizationService userOrganizationService;

    public EventNotificationService(
            NotificationSenderService notificationSenderService,
            NotificationService notificationService,
            EventLineService eventLineService,
            UserOrganizationService userOrganizationService
    ) {
        this.notificationSenderService = notificationSenderService;
        this.notificationService = notificationService;
        this.eventLineService = eventLineService;
        this.userOrganizationService = userOrganizationService;
    }

    public void sendEventCanceledNotification(Event event) {
        sendLineManagerCancelNotification(event);
        sendAssignedOrganizationsCancelNotification(event);
    }

    private void sendAssignedOrganizationsCancelNotification(Event event) {
        boolean hasNext;
        int page = 0;
        do {
            Page<EventLine> allLinesOfEvent = eventLineService.getAllLinesOfEvent(event.getId(), page);
            hasNext = allLinesOfEvent.hasNext();
            page++;
            for (EventLine eventLine : allLinesOfEvent.getContent()) {
                sendCancelNotificationForEventLine(eventLine);
            }
        } while (hasNext);
    }

    private void sendCancelNotificationForEventLine(EventLine eventLine) {
        if (eventLine.getEventLineStatus() == EventLineStatus.CANCELED
                || eventLine.getAssignedUsers() == null
                || eventLine.getAssignedUsers().size() == 0
                || eventLine.getOrganization() == null
        ) {
            return;
        }
        sendOrganizationLeadersCancelNotification(eventLine.getOrganization(), eventLine);
        eventLine.getAssignedUsers().forEach(user -> sendMemberCancelNotification(user, eventLine));
    }

    private void sendOrganizationLeadersCancelNotification(Organization organization, EventLine eventLine) {
        List<UserOrganization> leaders = userOrganizationService.getAllOrganizationLeadersByOrganizationId(organization.getId());
        leaders.forEach(leader -> sendOrganizationLeaderCancelNotification(leader, eventLine));
    }

    private void sendOrganizationLeaderCancelNotification(UserOrganization leader, EventLine eventLine) {
        User user = leader.getUser();
        String body = String.format(
                "Hello %s %s\n\nEvent %s on %s for which your organization was assigned to %s has been canceled." +
                "Your organization's services are no longer required and any member you may have had assigned have been notified.",
                user.getFirstname(),
                user.getLastname(),
                eventLine.getEvent().getName(),
                eventLine.getEvent().getDate().toLocalDate(),
                eventLine.getLine().getName()
        );

        sendOrganizationMemberCanceledNotification(user, body);
    }

    private void sendMemberCancelNotification(User user, EventLine eventLine) {
        String body = String.format("Hello %s %s\n\nEvent %s on %s was canceled and you were assigned to work %s, as the event is canceled this work is no longer needed.",
                user.getFirstname(),
                user.getLastname(),
                eventLine.getEvent().getName(),
                eventLine.getEvent().getDate().toLocalDate(),
                eventLine.getLine().getName()
        );

        sendOrganizationMemberCanceledNotification(user, body);
    }

    private void sendOrganizationMemberCanceledNotification(User user, String body) {
        Notification notification = Notification.builder()
                .userId(user.getId())
                .type(NotificationType.EVENT_CANCELED)
                .urgency(NotificationUrgency.NORMAL)
                .creationDate(LocalDateTime.now())
                .title("Event canceled")
                .body(body)
                .build();
        saveAndSendNotification(user, notification);
    }

    private void saveAndSendNotification(User user, Notification notification) {
        notificationService.saveNotificationAndSendToInbox(notification, user);
        notificationSenderService.sendNotification(notification);
    }

    private void sendLineManagerCancelNotification(Event event) {
        List<User> lineManagers = event.getVenue().getLineManagers();
        for (User lineManager : lineManagers) {
            Notification notification = createLineManagerCancelEventNotification(event, lineManager);
            saveAndSendNotification(lineManager, notification);
        }
    }

    private Notification createLineManagerCancelEventNotification(Event event, User lineManager) {
        return Notification.builder()
            .title(String.format("event %s is canceled", event.getName()))
            .body(String.format("event %s is canceled, this event was planned on %s ", event.getName(), event.getDate()))
            .creationDate(LocalDateTime.now())
            .urgency(NotificationUrgency.NORMAL)
            .usedChannel(NotificationChannel.EMAIL)
            .type(NotificationType.EVENT_CANCELED)
            .userId(lineManager.getId())
            .build();
    }

    public void eventCreated(Event event) {
        if (event.getVenue().getLineManagers().size() == 0) {
            return;
        }

        String body = generateEventCreatedBody(event);
        for (User lineManager : event.getVenue().getLineManagers()) {
            sendEventCreatedNotificationToUser(lineManager, body);
        }
    }

    private String generateEventCreatedBody(Event event) {
        LocalDateTime eventDate = event.getDate();
        return String.format("New Event: %s at %s on %s at %s",
            event.getName(),
            event.getVenue().getName(),
            eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            eventDate.format(DateTimeFormatter.ISO_LOCAL_TIME)
        );
    }

    private void sendEventCreatedNotificationToUser(User lineManager, String body) {
        Notification notification = Notification.builder()
                .userId(lineManager.getId())
                .type(NotificationType.EVENT_CREATED)
                .urgency(NotificationUrgency.NORMAL)
                .creationDate(LocalDateTime.now())
                .title("New event created")
                .body(body)
                .build();
        notificationService.saveNotificationAndSendToQueue(notification);
    }
}
