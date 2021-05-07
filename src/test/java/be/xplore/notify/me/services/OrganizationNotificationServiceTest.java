package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
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
class OrganizationNotificationServiceTest {
    @Autowired
    private OrganizationNotificationService organizationNotificationService;

    @MockBean
    private NotificationService notificationService;
    @Autowired
    private User user;
    @Autowired
    private Organization organization;
    @Autowired
    private EventLine eventLine;
    private Notification notification;
    private UserOrganization userOrganization;

    private void setupMocking() {
        given(notificationService.saveNotificationAndSendToInbox(any(), any())).will(i -> {
            notification = i.getArgument(0);
            return notification;
        });
    }

    @BeforeEach
    void setUp() {
        userOrganization = UserOrganization.builder()
            .id("1")
            .user(user)
            .organization(organization)
            .role(Role.ORGANIZATION_LEADER)
            .build();
        setupMocking();
    }

    @Test
    void sendOrganizationLineAssignmentNotification() {
        organizationNotificationService.sendOrganizationLineAssignmentNotification(userOrganization.getOrganization(), eventLine);
        Assertions.assertEquals(NotificationType.LINE_ASSIGNED, notification.getType());
    }
}