package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventNotificationService {

    private final NotificationService notificationService;

    public EventNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
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
