package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.dto.notification.NotificationDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper implements DtoMapper<NotificationDto, Notification> {
    @Override
    public Notification fromDto(NotificationDto d) {
        return Notification.builder().id(d.getId()).build();
    }

    @Override
    public NotificationDto toDto(Notification d) {
        return new NotificationDto(
            d.getId(), d.getUserId(), d.getTitle(), d.getBody(), d.getEventId(), d.getUsedChannel(),
            d.getType(), d.getUrgency(), d.getCreationDate(), d.getPrice(), d.getPriceCurrency());
    }
}
