package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        Notification savedNotification = save(sendNotificationWithoutInbox(notification, user));
        userService.addNotificationToInbox(savedNotification, user);
        return savedNotification;
    }

    public Notification sendNotificationWithoutInbox(Notification notification, User user) {
        return senderService.sendNotification(notification, user);
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

    public Page<Notification> getAllNotificationsByUserId(String userId, PageRequest pageRequest) {
        return notificationRepo.getAllByUserId(userId, pageRequest);
    }

    public Page<Notification> getAllNotifications(PageRequest pageRequest) {
        return notificationRepo.getAll(pageRequest);
    }

    public Page<Notification> getAllNotificationsByType(NotificationType notificationType, PageRequest pageRequest) {
        return notificationRepo.getAllByNotificationType(notificationType, pageRequest);
    }

    public Page<Notification> getAllNotificationsByEventId(String eventId, PageRequest pageRequest) {
        return notificationRepo.getAllByEventId(eventId, pageRequest);
    }

    public Page<Notification> getAllByTypeAndEvent(String eventId, NotificationType notificationType, PageRequest pageRequest) {
        return notificationRepo.getAllByTypeAndEvent(notificationType, eventId, pageRequest);
    }

    public List<String> getAllEventIds() {
        return notificationRepo.getAllEventIds();
    }

    public List<Object[]> getChannelAmount() {
        return notificationRepo.getChannelAmount();
    }

    public static NotificationUrgency getNormalNotificationUrgency(Event event) {
        NotificationUrgency notificationUrgency = NotificationUrgency.NORMAL;
        if (isUrgent(event)) {
            notificationUrgency = NotificationUrgency.URGENT;
        }
        return notificationUrgency;
    }

    public static boolean isUrgent(Event event) {
        return event.getDate().minusDays(2).isBefore(LocalDateTime.now());
    }
}
