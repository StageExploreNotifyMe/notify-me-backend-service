package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventNotificationService {
    private final NotificationSenderService notificationSenderService;
    private final NotificationService notificationService;
    private final EventLineService eventLineService;

    public EventNotificationService(NotificationSenderService notificationSenderService, NotificationService notificationService, EventLineService eventLineService) {
        this.notificationSenderService = notificationSenderService;
        this.notificationService = notificationService;
        this.eventLineService = eventLineService;
    }

    public void sendEventCanceledNotification(Event event) {
        List<User> lineManagers = eventLineService.getLineManagersByEvent(event);
        for (User lineManager : lineManagers) {
            Notification notification = setEventCanceledNotificationDetails(event, lineManager);
            notificationService.saveNotificationAndSendToInbox(notification, lineManager);
            notificationSenderService.sendNotification(notification);
        }
    }

    private Notification setEventCanceledNotificationDetails(Event event, User lineManager) {
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
}
