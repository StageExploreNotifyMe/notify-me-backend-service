package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.services.RepoService;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService extends RepoService<Notification, NotificationEntity> {

    private final UserService userService;

    public NotificationService(JpaRepository<NotificationEntity, String> repo, EntityMapper<NotificationEntity, Notification> entityMapper, UserService userService) {
        super(repo, entityMapper);
        this.userService = userService;
    }

    public Notification saveNotificationAndSendToInbox(Notification notification) {
        Notification savedNotification = save(notification);
        userService.addNotificationToInbox(savedNotification);
        return savedNotification;
    }

}
