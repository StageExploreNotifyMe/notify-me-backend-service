package be.xplore.notify.me.util.mockadapters.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.persistence.NotificationRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationAdapter implements NotificationRepo {

    @Override
    public Page<Notification> getAllByUserId(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public Notification save(Notification notification) {
        return null;
    }

    @Override
    public Optional<Notification> findById(String id) {
        return Optional.empty();
    }
}
