package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class OrganizationNotificationServiceTest {
    @Autowired
    private OrganizationNotificationService organizationNotificationService;

    @MockBean
    private NotificationService notificationService;
    @MockBean
    private UserOrganizationService userOrganizationService;
    @Autowired
    private User user;
    @Autowired
    private Organization organization;
    @Autowired
    private EventLine eventLine;
    private Notification notification;
    private UserOrganization userOrganization;

    private void setupMocking() {
        given(notificationService.sendNotification(any(), any())).will(i -> {
            notification = i.getArgument(0);
            return notification;
        });
        given(userOrganizationService.getAllOrganizationLeadersByOrganizationId(any())).will(i -> {
            List<UserOrganization> userOrganizationList = new ArrayList<>();
            userOrganizationList.add(userOrganization);
            return userOrganizationList;
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