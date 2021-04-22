package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.springframework.stereotype.Service;

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

        Notification notification = new Notification();
        notification.setUser(user);

        setResolvedTitleAndBody(userOrganization, user, notification);
        setResolvedNotificationDetails(userOrganization, user, notification);

        notificationService.saveNotificationAndSendToInbox(notification);
        notificationSenderService.sendNotification(notification);
    }

    private void setResolvedNotificationDetails(UserOrganization userOrganization, User user, Notification notification) {
        notification.setType(userOrganization.getStatus() == MemberRequestStatus.ACCEPTED ? NotificationType.USER_JOINED : NotificationType.USER_DECLINED);
        notification.setUrgency(NotificationUrgency.NORMAL);
        notification.setUsedChannel(user.getUserPreferences().getNormalChannel());
    }

    private void setResolvedTitleAndBody(UserOrganization userOrganization, User user, Notification notification) {
        String title = String.format("Request to join %s %s", userOrganization.getOrganization().getName(), userOrganization.getStatus().toString());
        String body = String.format("%s %s", user.getFirstname(), user.getLastname());

        notification.setTitle(title);
        notification.setBody(body);
    }

}
