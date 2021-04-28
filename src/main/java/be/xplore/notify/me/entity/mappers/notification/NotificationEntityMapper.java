package be.xplore.notify.me.entity.mappers.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper implements EntityMapper<NotificationEntity, Notification> {

    @Override
    public Notification fromEntity(NotificationEntity notificationEntity) {
        return Notification.builder()
            .id(notificationEntity.getId())
            .userId(notificationEntity.getUserId())
            .type(notificationEntity.getType())
            .urgency(notificationEntity.getUrgency())
            .body(notificationEntity.getBody())
            .title(notificationEntity.getTitle())
            .usedChannel(notificationEntity.getUsedChannel())
            .dateTime(notificationEntity.getDateTime())
            .build();
    }

    @Override
    public NotificationEntity toEntity(Notification notification) {
        return new NotificationEntity(
                notification.getId(),
                notification.getUserId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getUsedChannel(),
                notification.getType(),
                notification.getUrgency(),
                notification.getDateTime()
        );
    }
}
