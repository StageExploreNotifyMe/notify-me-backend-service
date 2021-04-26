package be.xplore.notify.me.domain.notification;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notification {
    String id;
    String userId;
    String title;
    String body;
    NotificationChannel usedChannel;
    NotificationType type;
    NotificationUrgency urgency;

    @Override
    public String toString() {
        return "Notification: " + title + ": " + body + "(" + usedChannel + ", " + type + ", " + urgency + ")";
    }
}
