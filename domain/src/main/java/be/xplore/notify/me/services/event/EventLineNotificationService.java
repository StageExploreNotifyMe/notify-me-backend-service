package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventLineNotificationService {
    private final NotificationService notificationService;
    private final UserOrganizationService userOrganizationService;

    public EventLineNotificationService(
            NotificationService notificationService,
            UserOrganizationService userOrganizationService
    ) {
        this.notificationService = notificationService;
        this.userOrganizationService = userOrganizationService;
    }

    public void sendMemberCanceledNotification(String userId, EventLine line) {
        List<UserOrganization> userOrganizations = userOrganizationService.getAllOrganizationLeadersByOrganizationId(line.getOrganization().getId());
        for (UserOrganization userOrganization: userOrganizations) {
            Notification notification = MemberCanceledDetails(userId, line, userOrganization.getUser());
            notificationService.sendNotification(notification, userOrganization.getUser());
        }

    }

    private Notification MemberCanceledDetails(String userId, EventLine line, User organizationLeader) {
        return Notification.builder()
            .title(String.format("user: %s canceled his attendance", userId))
            .body(String.format("user: %s canceled his attendance for %s at %s", userId, line.getLine().getName(), line.getEvent().getName()))
            .type(NotificationType.USER_CANCELED)
            .userId(organizationLeader.getId())
            .urgency(NotificationUrgency.NORMAL)
            .creationDate(LocalDateTime.now())
            .usedChannel(NotificationChannel.EMAIL)
            .build();

    }

    public void sendEventLineCanceledNotification(EventLine eventLine) {
        Notification notification = setEventLineCanceledDetails(eventLine);
        notificationService.sendNotification(notification, eventLine.getLineManager());
    }

    private Notification setEventLineCanceledDetails(EventLine eventLine) {
        return Notification.builder()
            .title(String.format("eventLine %s is %s", eventLine.getLine().getName(), eventLine.getEventLineStatus()))
            .body(String.format("eventLine %s is %s ", eventLine.getLine().getName(), eventLine.getEventLineStatus()))
            .creationDate(LocalDateTime.now())
            .urgency(NotificationUrgency.NORMAL)
            .usedChannel(NotificationChannel.EMAIL)
            .type(NotificationType.LINE_CANCELED)
            .userId(eventLine.getLineManager().getId())
            .build();
    }

    public void notifyLineAssigned(User user, EventLine line) {
        Notification toSave = Notification.builder()
                .userId(user.getId())
                .title("You've been assigned to a line")
                .body(createLineAssignedBody(user, line))
                .creationDate(LocalDateTime.now())
                .urgency(NotificationUrgency.NORMAL)
                .usedChannel(user.getUserPreferences().getNormalChannel())
                .type(NotificationType.LINE_ASSIGNED)
                .build();

        notificationService.sendNotification(toSave, user);
    }

    private String createLineAssignedBody(User user, EventLine line) {
        LocalDateTime eventDate = line.getEvent().getDate();

        return String.format("Hi %s %s\n\nYou've been assigned to work at event %s at %s on %s at %s.",
            user.getFirstname(), user.getLastname(),
            line.getEvent().getName(),
            line.getLine().getName(),
            eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            eventDate.format(DateTimeFormatter.ISO_LOCAL_TIME)
        );
    }
}
