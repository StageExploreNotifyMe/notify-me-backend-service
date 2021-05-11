package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Autowired
    private Notification notification;
    @Autowired
    private User user;

    private void mockGetById() {
        given(notificationRepo.findById(any()))
                .will(i -> i.getArgument(0).equals(notification.getId()) ? Optional.of(notification) : Optional.empty());
    }

    private void mockAddNotificationToInbox() {
        given(userService.addNotificationToInbox(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            user.getInbox().add(n);
            return user;
        });
    }

    private void mockAddNotificationToQueue() {
        given(userService.addNotificationToInbox(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            user.getNotificationQueue().add(n);
            return user;
        });
    }

    private void mockSaveNotification() {
        given(notificationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void saveNotificationAndSendToInbox() {
        mockAddNotificationToInbox();
        mockSaveNotification();
        Notification returnedNotification = notificationService.saveNotificationAndSendToInbox(notification, user);
        assertEquals(notification.getId(), returnedNotification.getId());
        assertTrue(user.getInbox().stream().anyMatch(n -> n.getId().equals(notification.getId())));
    }

    @Test
    void saveNotificationAndSendToQueue() {
        mockAddNotificationToQueue();
        mockSaveNotification();
        Notification returnedNotification = notificationService.saveNotificationAndSendToQueue(notification);
        assertEquals(notification.getId(), returnedNotification.getId());
        assertTrue(user.getInbox().stream().anyMatch(n -> n.getId().equals(notification.getId())));
    }

    @Test
    void getById() {
        mockGetById();
        Optional<Notification> notificationOptional = notificationService.getById(notification.getId());
        assertTrue(notificationOptional.isPresent());
        assertEquals(notification.getId(), notificationOptional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        mockGetById();
        Optional<Notification> notificationOptional = notificationService.getById("qdsf");
        assertTrue(notificationOptional.isEmpty());
    }
}