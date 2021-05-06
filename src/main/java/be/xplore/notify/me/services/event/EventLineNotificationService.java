package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventLineNotificationService {
    private final NotificationSenderService notificationSenderService;
    private final NotificationService notificationService;

    public EventLineNotificationService(NotificationSenderService notificationSenderService, NotificationService notificationService) {
        this.notificationSenderService = notificationSenderService;
        this.notificationService = notificationService;
    }

    public void sendEventLineCanceledNotification(EventLine eventLine) {
        Notification notification = setEventLineCanceledDetails(eventLine);
        notificationService.saveNotificationAndSendToInbox(notification);
        notificationSenderService.sendNotification(notification);
    }

    private Notification setEventLineCanceledDetails(EventLine eventLine) {
        return Notification.builder()
            .id(eventLine.getId())
            .title(String.format("eventLine %s is %s", eventLine.getLine().getName(), eventLine.getEventLineStatus()))
            .body(String.format("eventLine %s is %s ", eventLine.getLine().getName(), eventLine.getEventLineStatus()))
            .creationDate(LocalDateTime.now())
            .urgency(NotificationUrgency.NORMAL)
            .usedChannel(NotificationChannel.EMAIL)
            .type(NotificationType.LINE_CANCELED)
            .userId(eventLine.getLineManager().getId())
            .build();
    }

}
