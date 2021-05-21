package be.xplore.notify.me.adapter;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.SendgridEmailService;
import be.xplore.notify.me.services.TwilioTextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class TwilioNotificationSenderAdapterTest {

    @Autowired
    private TwilioNotificationSenderAdapter senderAdapter;

    @MockBean
    private TwilioTextService twilioTextService;
    @MockBean
    private SendgridEmailService sendgridEmailService;

    @Autowired
    private User user;
    @Autowired
    private Notification notification;

    private NotificationChannel calledChannel;

    private Notification generateNotification(NotificationChannel channel) {
        return Notification.builder()
            .usedChannel(channel)
            .id(notification.getId())
            .creationDate(LocalDateTime.now())
            .type(notification.getType())
            .body(notification.getBody())
            .title(notification.getTitle())
            .urgency(notification.getUrgency())
            .userId(user.getId())
            .build();
    }

    @BeforeEach
    void setUp() {
        calledChannel = null;
        mockSend(twilioTextService.sendSms(any(), any()), NotificationChannel.SMS);
        mockSend(twilioTextService.sendWhatsApp(any(), any()), NotificationChannel.WHATSAPP);
        mockSend(sendgridEmailService.sendEmail(any(), any()), NotificationChannel.EMAIL);
    }

    private void mockSend(Notification notification, NotificationChannel channel) {
        given(notification).will(i -> {
            calledChannel = channel;
            return i.getArgument(0);
        });
    }

    @Test
    void sendNotification() {
        for (NotificationChannel value : NotificationChannel.values()) {
            Notification sentNotification = senderAdapter.sendNotification(generateNotification(value), user);
            assertEquals(notification.getId(), sentNotification.getId());
            if (value != NotificationChannel.APP) {
                assertEquals(calledChannel, value);
            }
        }
    }

    @Test
    void sendNotificationWrongUserId() {
        assertThrows(IllegalArgumentException.class, () -> senderAdapter.sendNotification(Notification.builder().userId("qmdfkljs").build(), user));
    }
}