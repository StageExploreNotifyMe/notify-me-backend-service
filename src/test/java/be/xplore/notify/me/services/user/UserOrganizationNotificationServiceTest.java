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
    private UserOrganization.UserOrganizationBuilder userOrganizationBuilder;
    private Notification sendNotification;

    private void setupMocking() {
        given(notificationService.saveNotificationAndSendToInbox(any(), any())).will(i -> {
            sendNotification = i.getArgument(0);
            return sendNotification;
        });
    }

    @BeforeEach
    void setUp() {
        UserPreferences userPreferences = UserPreferences.builder().id("1").normalChannel(NotificationChannel.EMAIL).urgentChannel(NotificationChannel.SMS).build();
        user = User.builder().id("1").userPreferences(userPreferences).firstname("John").lastname("Doe").build();
        organization = Organization.builder().id("1").name("Example Organization").build();
        userOrganizationBuilder = UserOrganization.builder().id("1").user(user).organization(organization).role(Role.MEMBER);

        sendNotification = null;
        setupMocking();
    }

    @Test
    void sendResolvedPendingRequestNotificationAccepted() {
        UserOrganization userOrganization = userOrganizationBuilder.status(MemberRequestStatus.ACCEPTED).build();
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(userOrganization);

        Assertions.assertEquals(NotificationType.USER_JOINED, sendNotification.getType());
    }

    @Test
    void sendResolvedPendingRequestNotificationDenied() {
        UserOrganization userOrganization = userOrganizationBuilder.status(MemberRequestStatus.DECLINED).build();
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(userOrganization);
        Assertions.assertEquals(NotificationType.USER_DECLINED, sendNotification.getType());
    }

    @Test
    void sendOrganizationRoleChangeNotificationPromoted() {
        UserOrganization userOrganization = userOrganizationBuilder.role(Role.ORGANIZATION_LEADER).build();
        userOrganizationNotificationService.sendOrganizationRoleChangeNotification(userOrganization);
        Assertions.assertEquals(NotificationType.USER_PROMOTED, sendNotification.getType());
    }

    @Test
    void sendOrganizationRoleChangeNotificationDemoted() {
        UserOrganization userOrganization = userOrganizationBuilder.role(Role.MEMBER).build();
        userOrganizationNotificationService.sendOrganizationRoleChangeNotification(userOrganization);
        Assertions.assertEquals(NotificationType.USER_DEMOTED, sendNotification.getType());
    }
}