package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.AuthenticationCodeRepo;
import be.xplore.notify.me.services.notification.NotificationService;
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
class AuthenticationCodeServiceTest {

    @Autowired
    Notification notification;
    @Autowired
    User user;
    @Autowired
    List<AuthenticationCode> authenticationCodes;

    @Autowired
    AuthenticationCodeService authenticationCodeService;

    @MockBean
    AuthenticationCodeRepo authenticationCodeRepo;

    @MockBean
    NotificationService notificationService;

    private List<Notification> sentNotifications;

    @BeforeEach
    void setUp() {
        sentNotifications = new ArrayList<>();
    }

    @Test
    void generateUserAuthCodes() {
        given(authenticationCodeRepo.saveAll(any())).will(i -> i.getArgument(0));
        List<AuthenticationCode> authenticationCodes = authenticationCodeService.generateUserAuthCodes();
        assertEquals(authenticationCodes.size(), 2);
        assertTrue(authenticationCodes.stream().anyMatch(a -> a.getNotificationChannel().equals(NotificationChannel.EMAIL)));
        assertTrue(authenticationCodes.stream().anyMatch(a -> a.getNotificationChannel().equals(NotificationChannel.SMS)));
    }

    @Test
    void sendUserAuthCodes() {
        given(notificationService.sendNotificationWithoutInbox(any(), any())).will(i -> {
            Notification sendNot = i.getArgument(0);
            sentNotifications.add(sendNot);
            return sendNot;
        });
        authenticationCodeService.sendUserAuthCodes(user, authenticationCodes);
        assertEquals(sentNotifications.size(), authenticationCodes.size());
        for (AuthenticationCode authenticationCode: authenticationCodes) {
            assertTrue(sentNotifications.stream().anyMatch(n -> n.getBody().contains(authenticationCode.getCode())));
        }

    }
}