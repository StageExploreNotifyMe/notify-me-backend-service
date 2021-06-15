package be.xplore.notify.me.domain.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AuthenticationCode {
    String id;
    String code;
    NotificationChannel notificationChannel;
    LocalDateTime creationDate;
}
