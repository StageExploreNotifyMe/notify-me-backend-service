package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, String> {
}
