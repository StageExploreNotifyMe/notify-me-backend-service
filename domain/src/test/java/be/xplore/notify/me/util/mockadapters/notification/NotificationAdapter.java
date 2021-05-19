package be.xplore.notify.me.util.mockadapters.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.persistence.NotificationRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    public Page<Notification> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Notification> getAllByNotificationType(NotificationType notificationType, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Notification> getAllByEventId(String eventId, Pageable pageabl) {
        return null;
    }

    @Override
    public Page<Notification> getAllByTypeAndEvent(NotificationType notificationType, String eventId, Pageable pageable) {
        return null;
    }

    @Override
    public List<String> getAllEventIds() {
        return null;
    }

    @Override
    public List<Object[]> getChannelAmount() {
        return null;
    }
}
