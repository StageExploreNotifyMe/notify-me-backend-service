package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.repositories.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationRepo notificationRepo;
    private final NotificationEntityMapper notificationEntityMapper;

    public NotificationService(UserService userService, NotificationRepo notificationRepo, NotificationEntityMapper notificationEntityMapper) {
        this.userService = userService;
        this.notificationRepo = notificationRepo;
        this.notificationEntityMapper = notificationEntityMapper;
    }

    public Notification saveNotificationAndSendToInbox(Notification notification) {
        Notification savedNotification = save(notification);
        userService.addNotificationToInbox(savedNotification);
        return savedNotification;
    }

    public Optional<Notification> getById(String id) {
        Optional<NotificationEntity> optional = notificationRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Notification notification = notificationEntityMapper.fromEntity(optional.get());
        return Optional.of(notification);
    }

    public Notification save(Notification notification) {
        NotificationEntity notificationEntity = notificationRepo.save(notificationEntityMapper.toEntity(notification));
        return notificationEntityMapper.fromEntity(notificationEntity);
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
