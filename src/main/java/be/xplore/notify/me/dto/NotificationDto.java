package be.xplore.notify.me.dto;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    String id;
    String userId;
    String title;
    String body;
    NotificationChannel usedChannel;
    NotificationType type;
    NotificationUrgency urgency;
    LocalDateTime dateTime;

}
