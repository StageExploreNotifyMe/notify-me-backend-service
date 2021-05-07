package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserOrganizationNotificationService {

    private final NotificationSenderService notificationSenderService;
    private final NotificationService notificationService;

    public UserOrganizationNotificationService(NotificationSenderService notificationSenderService, NotificationService notificationService) {
        this.notificationSenderService = notificationSenderService;
        this.notificationService = notificationService;
    }

    public void sendResolvedPendingRequestNotification(UserOrganization userOrganization) {
        User user = userOrganization.getUser();
        Notification notification = setResolvedNotificationDetails(userOrganization, user);
        notificationService.saveNotificationAndSendToInbox(notification);
        notificationSenderService.sendNotification(notification);
    }

    private Notification setResolvedNotificationDetails(UserOrganization userOrganization, User user) {
        return Notification.builder()
            .title(String.format("Request to join %s %s", userOrganization.getOrganization().getName(), userOrganization.getStatus().toString()))
            .body(String.format("%s %s", user.getFirstname(), user.getLastname()))
            .type(userOrganization.getStatus() == MemberRequestStatus.ACCEPTED ? NotificationType.USER_JOINED : NotificationType.USER_DECLINED)
            .urgency(NotificationUrgency.NORMAL)
            .usedChannel(user.getUserPreferences().getNormalChannel())
            .userId(user.getId())
            .build();
    }

    public void sendOrganizationRoleChangeNotification(UserOrganization userOrganization) {
        User user = userOrganization.getUser();
        NotificationType notificationType = NotificationType.USER_PROMOTED;
        if (userOrganization.getRole().equals(Role.MEMBER)) {
            notificationType = NotificationType.USER_DEMOTED;
        }
        Notification notification = setChangedRoleNotificationDetails(userOrganization, user, notificationType);
        notificationService.saveNotificationAndSendToInbox(notification);
        notificationSenderService.sendNotification(notification);
    }

    private Notification setChangedRoleNotificationDetails(UserOrganization userOrganization, User user, NotificationType notificationType) {
        return Notification.builder()
            .title("Changed member role")
            .body(String.format("The organization changed your member role to %s", userOrganization.getRole()))
            .usedChannel(user.getUserPreferences().getNormalChannel())
            .creationDate(LocalDateTime.now())
            .urgency(NotificationUrgency.NORMAL)
            .type(notificationType)
            .userId(user.getId())
            .build();
    }
}
