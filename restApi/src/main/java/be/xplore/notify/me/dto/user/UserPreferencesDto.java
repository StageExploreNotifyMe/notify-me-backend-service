package be.xplore.notify.me.dto.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDto {
    private String id;
    private NotificationChannel normalChannel;
    private NotificationChannel urgentChannel;
}
