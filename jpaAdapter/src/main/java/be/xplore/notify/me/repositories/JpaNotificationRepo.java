package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNotificationRepo extends JpaRepository<NotificationEntity, String> {
    Page<NotificationEntity> getAllByUserId(String userId, Pageable pageable);

    Page<NotificationEntity> getAllByType(NotificationType notificationType, Pageable pageable);

    Page<NotificationEntity> getAllByEventId(String eventId, Pageable pageable);

    Page<NotificationEntity> getAllByEventIdAndType(String eventId, NotificationType notificationType, Pageable pageable);
}
