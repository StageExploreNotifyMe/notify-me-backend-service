package be.xplore.notify.me.util.mockadapters.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import org.springframework.stereotype.Component;

@Component
public class MockNotificationSenderServiceAdapter implements NotificationSenderService {
    @Override
    public Notification sendNotification(Notification notification, User user) {
        return null;
    }
}
