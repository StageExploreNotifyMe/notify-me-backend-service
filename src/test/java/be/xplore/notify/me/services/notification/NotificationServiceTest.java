package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.repositories.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private NotificationRepo notificationRepo;
    @MockBean
    private UserService userService;

    private Notification notification;
    private User user;

    private void mockAddNotificationToInbox() {
        given(userService.addNotificationToInbox(any())).will(i -> {
            Notification n = i.getArgument(0);
            n.getUser().getInbox().add(n);
            return n.getUser();
        });
    }

    private void mockSaveNotification() {
        given(notificationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @BeforeEach
    void setUp() {
        user = new User("1", new UserPreferences("1", NotificationChannel.EMAIL, NotificationChannel.SMS), "John", "Doe", new ArrayList<>());
        notification = new Notification("1", user, "Test", "This is a test", NotificationChannel.EMAIL, NotificationType.USER_JOINED, NotificationUrgency.NORMAL);
    }

    @Test
    void saveNotificationAndSendToInbox() {
        mockAddNotificationToInbox();
        mockSaveNotification();
        Notification returnedNotification = notificationService.saveNotificationAndSendToInbox(notification);
        assertEquals(notification, returnedNotification);
        assertTrue(user.getInbox().contains(notification));
    }

    @Test
    void saveNotificationAndSendToInboxThrowsDbException() {
        mockAddNotificationToInbox();
        given(notificationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> notificationService.saveNotificationAndSendToInbox(notification));
    }
}