package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MockNotificationSenderService implements NotificationSenderService {
    @Override
    public void sendNotification(Notification notification) {
        log.trace("Notification send: " + notification);
        log.trace("User Inbox:");
        List<Notification> inbox = notification.getUser().getInbox();
        for (int i = 0; i < inbox.size(); i++) {
            log.trace(i + ": " + inbox.get(i));
        }
    }
}
