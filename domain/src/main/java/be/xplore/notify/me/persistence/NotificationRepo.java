package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepo {
    Page<Notification> getAllByUserId(String userId, Pageable pageable);

    Notification save(Notification notification);

    Optional<Notification> findById(String id);
}
