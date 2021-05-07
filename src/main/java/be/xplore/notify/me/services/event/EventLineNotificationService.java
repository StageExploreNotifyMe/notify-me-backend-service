package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventLineNotificationService {

    private final NotificationSenderService notificationSenderService;
    private final NotificationService notificationService;

    public EventLineNotificationService(NotificationSenderService notificationSenderService, NotificationService notificationService) {
        this.notificationSenderService = notificationSenderService;
        this.notificationService = notificationService;
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

        Notification notification = notificationService.saveNotificationAndSendToInbox(toSave, user);
        notificationSenderService.sendNotification(notification);
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
