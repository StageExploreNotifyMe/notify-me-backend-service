package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockNotificationSenderService implements NotificationSenderService {
    @Override
    public void sendNotification(Notification notification) {
        log.trace("Notification send: " + notification);
    }
}
