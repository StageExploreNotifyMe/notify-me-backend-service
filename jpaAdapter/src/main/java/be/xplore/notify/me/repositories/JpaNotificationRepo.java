package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaNotificationRepo extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> getAllByUserId(String userId, Pageable pageable);

    Page<NotificationEntity> getAllByType(NotificationType notificationType, Pageable pageable);

    Page<NotificationEntity> getAllByEventId(String eventId, Pageable pageable);

    Page<NotificationEntity> getAllByEventIdAndType(String eventId, NotificationType notificationType, Pageable pageable);

    @Query("select count(n) as amount, n.usedChannel from NotificationEntity n group by n.usedChannel")
    List<Object[]> getChannelAmount();

}
