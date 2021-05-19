package be.xplore.notify.me.domain.notification;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Notification {
    String id;
    String userId;
    String title;
    String body;
    String eventId;
    NotificationChannel usedChannel;
    NotificationType type;
    NotificationUrgency urgency;
    LocalDateTime creationDate;

    @Override
    public String toString() {
        return "Notification: " + title + ": " + body + "(" + usedChannel + ", " + type + ", " + urgency + ")";
    }
}
