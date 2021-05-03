package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.repositories.NotificationRepo;
import be.xplore.notify.me.services.RepoService;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService extends RepoService<Notification, NotificationEntity> {

    private final UserService userService;
    private final NotificationRepo notificationRepo;

    public NotificationService(NotificationRepo repo, EntityMapper<NotificationEntity, Notification> entityMapper, UserService userService) {
        super(repo, entityMapper);
        this.userService = userService;
        this.notificationRepo = repo;
    }

    public Notification saveNotificationAndSendToInbox(Notification notification) {
        Notification savedNotification = save(notification);
        userService.addNotificationToInbox(savedNotification);
        return savedNotification;
    }

    public Page<Notification> getAllNotifications(String userId, PageRequest pageRequest) {
        try {
            Page<NotificationEntity> notifications = notificationRepo.getAllByUserId(userId, pageRequest);
            return notifications.map(entityMapper::fromEntity);
        } catch (Exception e) {
            log.error("Fetching all notifications for userId: {} failed: {}: {}", userId, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
