package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.repositories.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserService userService;

    public NotificationService(NotificationRepo notificationRepo, UserService userService) {
        this.notificationRepo = notificationRepo;
        this.userService = userService;
    }

    public Notification saveNotificationAndSendToInbox(Notification notification) {
        Notification savedNotification = save(notification);
        userService.addNotificationToInbox(savedNotification);
        return savedNotification;
    }

    public Notification save(Notification notification) {
        try {
            return notificationRepo.save(notification);
        } catch (Exception e) {
            log.error("Saving Notification failed {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
