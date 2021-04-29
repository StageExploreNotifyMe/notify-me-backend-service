package be.xplore.notify.me.domain.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserPreferences {
    String id;
    NotificationChannel normalChannel;
    NotificationChannel urgentChannel;
}
