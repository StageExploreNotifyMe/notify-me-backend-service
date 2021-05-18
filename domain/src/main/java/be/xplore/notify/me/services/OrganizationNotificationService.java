package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
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
public class OrganizationNotificationService {
    private final NotificationService notificationService;
    private final NotificationSenderService notificationSenderService;
    private final UserOrganizationService userOrganizationService;

    public OrganizationNotificationService(
            NotificationService notificationService,
            NotificationSenderService notificationSenderService,
            UserOrganizationService userOrganizationService
    ) {
        this.notificationService = notificationService;
        this.notificationSenderService = notificationSenderService;
        this.userOrganizationService = userOrganizationService;
    }

    public void sendOrganizationLineAssignmentNotification(Organization organization, EventLine line) {
        List<UserOrganization> userOrganizationLeaders = userOrganizationService.getAllOrganizationLeadersByOrganizationId(organization.getId());
        for (UserOrganization userOrganization : userOrganizationLeaders) {
            Notification notification = setOrganizationLineAssignmentNotificationDetails(organization, line, userOrganization.getUser());
            notificationService.saveNotificationAndSendToInbox(notification, userOrganization.getUser());
            notificationSenderService.sendNotification(notification);
        }

    }

    public Notification setOrganizationLineAssignmentNotificationDetails(Organization organization, EventLine line, User user) {
        return Notification.builder()
            .title(String.format("Your organization: %s is assigned to line %s", organization.getName(), line.getLine().getName()))
            .body(String.format("%s is assigned to line %s for: %s ,you can start searching for members",
                organization.getName(), line.getLine().getName(), line.getEvent().getName()))
            .userId(user.getId())
            .urgency(NotificationUrgency.NORMAL)
            .usedChannel(NotificationChannel.EMAIL)
            .creationDate(LocalDateTime.now())
            .type(NotificationType.LINE_ASSIGNED)
            .build();
    }
}
