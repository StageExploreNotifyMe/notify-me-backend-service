package be.xplore.notify.me.adapters.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.persistence.NotificationRepo;
import be.xplore.notify.me.repositories.JpaNotificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NotificationAdapter implements NotificationRepo {

    private final JpaNotificationRepo jpaNotificationRepo;
    private final NotificationEntityMapper notificationEntityMapper;

    public NotificationAdapter(JpaNotificationRepo jpaNotificationRepo, NotificationEntityMapper notificationEntityMapper) {
        this.jpaNotificationRepo = jpaNotificationRepo;
        this.notificationEntityMapper = notificationEntityMapper;
    }

    @Override
    public Page<Notification> getAllByUserId(String userId, Pageable pageable) {
        Page<NotificationEntity> notifications = jpaNotificationRepo.getAllByUserId(userId, pageable);
        return notifications.map(notificationEntityMapper::fromEntity);
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity notificationEntity = jpaNotificationRepo.save(notificationEntityMapper.toEntity(notification));
        return notificationEntityMapper.fromEntity(notificationEntity);
    }

    @Override
    public Optional<Notification> findById(String id) {
        Optional<NotificationEntity> optional = jpaNotificationRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Notification notification = notificationEntityMapper.fromEntity(optional.get());
        return Optional.of(notification);
    }

    @Override
    public Page<Notification> getAll(Pageable pageable) {
        Page<NotificationEntity> notifications = jpaNotificationRepo.findAll(pageable);
        return notifications.map(notificationEntityMapper::fromEntity);
    }

    @Override
    public Page<Notification> getAllByNotificationType(NotificationType notificationType, Pageable pageable) {
        Page<NotificationEntity> notifications = jpaNotificationRepo.getAllByType(notificationType, pageable);
        return notifications.map(notificationEntityMapper::fromEntity);
    }

    @Override
    public Page<Notification> getAllByEventId(String eventId, Pageable pageable) {
        Page<NotificationEntity> notifications = jpaNotificationRepo.getAllByEventId(eventId, pageable);
        return notifications.map(notificationEntityMapper::fromEntity);
    }

    @Override
    public Page<Notification> getAllByTypeAndEvent(NotificationType notificationType, String eventId, Pageable pageable) {
        Page<NotificationEntity> notifications = jpaNotificationRepo.getAllByEventIdAndType(eventId, notificationType, pageable);
        return notifications.map(notificationEntityMapper::fromEntity);
    }

    @Override
    public List<String> getAllEventIds() {
        return jpaNotificationRepo.findAll().stream().map(NotificationEntity::getEventId).collect(Collectors.toList());
    }

}
