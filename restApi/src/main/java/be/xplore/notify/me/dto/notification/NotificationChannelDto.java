package be.xplore.notify.me.dto.notification;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationChannelDto {
    List<NotificationChannel> notificationChannels;
}
