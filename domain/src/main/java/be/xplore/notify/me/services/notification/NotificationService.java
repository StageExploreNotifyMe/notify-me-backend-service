package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationRepo notificationRepo;
    private final NotificationSenderService senderService;

    public NotificationService(
            UserService userService,
            NotificationRepo notificationRepo,
            NotificationSenderService notificationSenderService
    ) {
        this.userService = userService;
        this.notificationRepo = notificationRepo;
        this.senderService = notificationSenderService;
    }

    public Notification sendNotification(Notification notification, User user) {
        Notification sendNotification = senderService.sendNotification(notification, user);
        Notification savedNotification = save(sendNotification);
        userService.addNotificationToInbox(savedNotification, user);
        return savedNotification;
    }

    public Notification queueNotification(Notification notification, User user) {
        Notification savedNotification = save(notification);
        userService.addNotificationToQueue(savedNotification, user);
        return savedNotification;
    }

    public Optional<Notification> getById(String id) {
        return notificationRepo.findById(id);
    }

    public Notification save(Notification notification) {
        return notificationRepo.save(notification);
    }

    public Page<Notification> getAllNotifications(String userId, PageRequest pageRequest) {
        return notificationRepo.getAllByUserId(userId, pageRequest);
    }

}
