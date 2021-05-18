package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class EventLineNotificationServiceTest {

    @Autowired
    private EventLineNotificationService eventLineNotificationService;

    @Autowired
    private User user;
    @Autowired
    private EventLine eventLine;
    @Autowired
    private UserOrganization userOrganization;

    private List<Notification> sendNotifications;

    @MockBean
    private NotificationService notificationService;
    @MockBean
    private NotificationSenderService notificationSenderService;
    @MockBean
    private UserOrganizationService userOrganizationService;

    @BeforeEach
    void setUp() {
        sendNotifications = new ArrayList<>();
    }

    @Test
    void notifyLineAssigned() {
        mockSaveNotification();
        eventLineNotificationService.notifyLineAssigned(user, eventLine);
        assertEquals(1, sendNotifications.size());
        Notification sendNotification = sendNotifications.get(0);
        assertEquals(NotificationType.LINE_ASSIGNED, sendNotification.getType());
    }

    @Test
    void sendMemberCanceledNotification() {
        mockGetUserOrganizations();
        mockSaveNotification();
        eventLineNotificationService.sendMemberCanceledNotification(user.getId(), eventLine);
        assertEquals(1, sendNotifications.size());
        Notification sendNotification = sendNotifications.get(0);
        assertEquals(NotificationType.USER_CANCELED, sendNotification.getType());
    }

    @Test
    void sendEventLineCanceledNotification() {
        mockSaveNotification();
        eventLineNotificationService.sendEventLineCanceledNotification(eventLine);
        assertEquals(1, sendNotifications.size());
        Notification sendNotification = sendNotifications.get(0);
        assertEquals(NotificationType.LINE_CANCELED, sendNotification.getType());
    }

    @Test
    void sendOrganizationLeadersStaffingReminder() {
        mockSaveNotification();
        eventLineNotificationService.sendOrganizationLeadersStaffingReminder(eventLine, createUserList(), "test");
        assertEquals(1, sendNotifications.size());
        assertEquals("test", sendNotifications.get(0).getBody());
        assertEquals(user.getId(), sendNotifications.get(0).getUserId());
    }

    @Test
    void sendOrganizationLeadersStaffingReminderWithoutBody() {
        mockSaveNotification();
        eventLineNotificationService.sendOrganizationLeadersStaffingReminder(eventLine, createUserList(), null);
        assertEquals(1, sendNotifications.size());
        assertTrue(sendNotifications.get(0).getBody().contains("Hi " + user.getFirstname()));
        assertEquals(user.getId(), sendNotifications.get(0).getUserId());
    }

    private List<User> createUserList() {
        List<User> users = new ArrayList<>();
        users.add(user);
        return users;
    }

    private void mockSaveNotification() {
        given(notificationService.sendNotification(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            sendNotifications.add(n);
            return n;
        });
    }

    private void mockGetUserOrganizations() {
        given(userOrganizationService.getAllOrganizationLeadersByOrganizationId(any())).will(i -> {
            List<UserOrganization> userOrganizations = new ArrayList<>();
            userOrganizations.add(userOrganization);
            return userOrganizations;
        });
    }
}