package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserOrganizationNotificationServiceTest {

    @Autowired
    private UserOrganizationNotificationService userOrganizationNotificationService;

    @MockBean
    private NotificationSenderService notificationSenderService;
    @MockBean
    private NotificationService notificationService;

    private User user;
    private Organization organization;

    private Notification sendNotification;

    private void setupMocking() {
        given(notificationService.saveNotificationAndSendToInbox(any())).will(i -> {
            sendNotification = i.getArgument(0);
            return sendNotification;
        });
    }

    @BeforeEach
    void setUp() {
        user = new User("1", new UserPreferences("1", NotificationChannel.EMAIL, NotificationChannel.SMS), "John", "Doe", new ArrayList<>());
        organization = new Organization("1", "Test Organization");
        sendNotification = null;
        setupMocking();
    }

    @Test
    void sendResolvedPendingRequestNotificationAccepted() {
        UserOrganization userOrganization = new UserOrganization("1", user, organization, Role.MEMBER, MemberRequestStatus.ACCEPTED);
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(userOrganization);

        Assertions.assertEquals(NotificationType.USER_JOINED, sendNotification.getType());
    }

    @Test
    void sendResolvedPendingRequestNotificationDenied() {
        UserOrganization userOrganization = new UserOrganization("1", user, organization, Role.MEMBER, MemberRequestStatus.DECLINED);
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(userOrganization);
        Assertions.assertEquals(NotificationType.USER_DECLINED, sendNotification.getType());
    }
}