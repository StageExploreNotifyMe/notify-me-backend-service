package be.xplore.notify.me.mappers.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.mappers.EntityMapper;
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
            .creationDate(notificationEntity.getCreationDate())
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
                notification.getCreationDate()
        );
    }
}
