package be.xplore.notify.me.services.scheduled;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class EmailScheduledServiceTest {

    @Autowired
    private EmailScheduledService emailScheduledService;

    @MockBean
    private UserService userService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private NotificationSenderService notificationSenderService;

    @Autowired
    private User user;
    @Autowired
    private Notification notification;
    private Notification sendNotification;

    @BeforeEach
    void setUp() {
        sendNotification = null;
    }

    @Test
    void run() {
        mockGetUsers();
        mockSaveAndSendNotification();
        emailScheduledService.run();
        assertNotNull(sendNotification);
        assertEquals(NotificationType.WEEKLY_DIGEST, sendNotification.getType());
        assertTrue(sendNotification.getBody().contains(notification.getBody()));
    }

    private void mockSaveAndSendNotification() {
        given(notificationService.saveNotificationAndSendToInbox(any())).will(i -> {
            sendNotification = i.getArgument(0);
            return sendNotification;
        });
    }

    private void mockGetUsers() {
        given(userService.getUsersPage(any())).will(i -> {
            List<User> users = new ArrayList<>();
            users.add(user);
            users.add(user);
            users.add(User.builder().id("5").firstname("test").lastname("user").notificationQueue(Collections.singletonList(notification)).build());
            return new PageImpl<>(users);
        });
    }
}