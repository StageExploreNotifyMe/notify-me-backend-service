package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventLineNotificationService {

    private final NotificationSenderService notificationSenderService;
    private final NotificationService notificationService;
    private final UserOrganizationService userOrganizationService;

    public EventLineNotificationService(
            NotificationSenderService notificationSenderService,
            NotificationService notificationService, UserOrganizationService userOrganizationService) {
        this.notificationSenderService = notificationSenderService;
        this.notificationService = notificationService;
        this.userOrganizationService = userOrganizationService;
    }

    public void sendMemberCanceledNotification(String userId, EventLine line) {
        List<UserOrganization> userOrganizations = userOrganizationService.getAllOrganizationLeadersByOrganizationId(line.getOrganization().getId());
        for (UserOrganization userOrganization: userOrganizations) {
            Notification notification = MemberCanceledDetails(userId, line, userOrganization.getUser());
            notificationSenderService.sendNotification(notification);
            notificationService.saveNotificationAndSendToInbox(notification);
        }

    }

    private Notification MemberCanceledDetails(String userId, EventLine line, User organizationLeader) {
        return Notification.builder()
            .title(String.format("user: %s canceled his attendance", userId))
            .body(String.format("user: %s canceled his attendance for %s at %s", userId, line.getLine().getName(), line.getEvent().getName()))
            .type(NotificationType.USER_CANCELED)
            .userId(organizationLeader.getId())
            .urgency(NotificationUrgency.NORMAL)
            .creationDate(LocalDateTime.now())
            .usedChannel(NotificationChannel.EMAIL)
            .build();
    }
}
