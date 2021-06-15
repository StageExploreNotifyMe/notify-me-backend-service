package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.AuthenticationCodeRepo;
import be.xplore.notify.me.services.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AuthenticationCodeService {

    private final AuthenticationCodeRepo repo;
    private final NotificationService notificationService;

    public AuthenticationCodeService(AuthenticationCodeRepo repo, @Lazy NotificationService notificationService) {
        this.repo = repo;
        this.notificationService = notificationService;
    }

    public List<AuthenticationCode> saveAll(List<AuthenticationCode> authenticationCodes) {
        return repo.saveAll(authenticationCodes);
    }

    public List<AuthenticationCode> generateUserAuthCodes() {

        List<AuthenticationCode> authenticationCodes = new ArrayList<>();
        for (NotificationChannel notificationChannel : Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.SMS)) {
            authenticationCodes.add(generateAuthCode(notificationChannel));
        }
        return saveAll(authenticationCodes);
    }

    public void sendUserAuthCodes(User user, List<AuthenticationCode> authenticationCodes) {
        authenticationCodes.forEach(code -> sendUserAuthCode(user, code));
    }

    public AuthenticationCode generateAuthCode(NotificationChannel channel) {
        return AuthenticationCode.builder()
            .code(String.format("%04d", new Random().nextInt(10000)))
            .creationDate(LocalDateTime.now())
            .notificationChannel(channel)
            .build();
    }

    public void sendUserAuthCode(User user, AuthenticationCode authenticationCode) {
        Notification notification = Notification.builder()
                .userId(user.getId())
                .usedChannel(authenticationCode.getNotificationChannel())
                .urgency(NotificationUrgency.NORMAL)
                .creationDate(LocalDateTime.now()).title("Confirmation token")
                .body(String.format("Hi %s %s your confirmation token is: %s", user.getFirstname(), user.getLastname(), authenticationCode.getCode()))
                .type(NotificationType.AUTHENTICATION_CODE)
                .build();
        notificationService.sendNotificationWithoutInbox(notification, user);
    }
}
