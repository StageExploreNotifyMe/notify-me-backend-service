package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private List<Notification> sendNotifications;

    @MockBean
    private NotificationService notificationService;
    @MockBean
    private NotificationSenderService notificationSenderService;

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

    private void mockSaveNotification() {
        given(notificationService.saveNotificationAndSendToInbox(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            sendNotifications.add(n);
            return n;
        });
    }
}