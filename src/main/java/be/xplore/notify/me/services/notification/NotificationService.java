package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.repositories.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotificationEntityMapper notificationEntityMapper;
    private final UserService userService;

    public NotificationService(NotificationRepo notificationRepo, NotificationEntityMapper notificationEntityMapper, UserService userService) {
        this.notificationRepo = notificationRepo;
        this.notificationEntityMapper = notificationEntityMapper;
        this.userService = userService;
    }

    public Notification saveNotificationAndSendToInbox(Notification notification) {
        Notification savedNotification = save(notification);
        userService.addNotificationToInbox(savedNotification);
        return savedNotification;
    }

    public Notification save(Notification notification) {
        try {
            NotificationEntity toSave = notificationEntityMapper.toEntity(notification);
            toSave.setDateTime(LocalDateTime.now());
            NotificationEntity save = notificationRepo.save(toSave);
            return notificationEntityMapper.fromEntity(save);
        } catch (Exception e) {
            log.error("Saving Notification failed {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public Page<Notification> getAllNotifications(String userId, PageRequest pageRequest) {
        try {
            Page<NotificationEntity> notifications = notificationRepo.getAllByUserId(userId, pageRequest);
            return notifications.map(notificationEntityMapper::fromEntity);
        } catch (Exception e) {
            log.error("Fetching all notifications for userId: {} failed: {}: {}", userId, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
