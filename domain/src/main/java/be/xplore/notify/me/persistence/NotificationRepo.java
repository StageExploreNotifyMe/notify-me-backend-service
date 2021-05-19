package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepo {
    Page<Notification> getAllByUserId(String userId, Pageable pageable);

    Notification save(Notification notification);

    Optional<Notification> findById(String id);

    Page<Notification> getAll(Pageable pageable);

    Page<Notification> getAllByNotificationType(NotificationType notificationType, Pageable pageable);

    Page<Notification> getAllByEventId(String eventId, Pageable pageable);

    Page<Notification> getAllByTypeAndEvent(NotificationType notificationType, String eventId, Pageable pageable);

    List<String> getAllEventIds();

    List<Object[]> getChannelAmount();
}
