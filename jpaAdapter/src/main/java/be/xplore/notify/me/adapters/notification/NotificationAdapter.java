package be.xplore.notify.me.adapters.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.repositories.JpaNotificationRepo;
import be.xplore.notify.me.persistence.NotificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

}
