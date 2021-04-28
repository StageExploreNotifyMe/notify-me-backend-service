package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.notification.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationEntity, String> {
    Page<NotificationEntity> getAllByUserId(String userId, Pageable pageable);
}
