package be.xplore.notify.me.mappers.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper implements EntityMapper<NotificationEntity, Notification> {

    @Override
    public Notification fromEntity(NotificationEntity notificationEntity) {
        return Notification.builder()
            .id(String.valueOf(notificationEntity.getId()))
            .userId(notificationEntity.getUserId())
            .type(notificationEntity.getType())
            .urgency(notificationEntity.getUrgency())
            .body(notificationEntity.getBody())
            .title(notificationEntity.getTitle())
            .usedChannel(notificationEntity.getUsedChannel())
            .creationDate(notificationEntity.getCreationDate())
            .eventId(notificationEntity.getEventId())
            .sentDate(notificationEntity.getSentDate())
            .price(notificationEntity.getPrice())
            .priceCurrency(notificationEntity.getPriceCurrency())
            .build();
    }

    @Override
    public NotificationEntity toEntity(Notification notification) {
        return new NotificationEntity(
                LongParser.parseLong(notification.getId()),
                notification.getUserId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getEventId(),
                notification.getUsedChannel(),
                notification.getType(),
                notification.getUrgency(),
                notification.getCreationDate(),
                notification.getSentDate(),
                notification.getPrice(),
                notification.getPriceCurrency()
        );
    }
}
