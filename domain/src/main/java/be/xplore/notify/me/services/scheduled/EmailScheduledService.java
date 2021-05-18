package be.xplore.notify.me.services.scheduled;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EmailScheduledService {

    private final UserService userService;
    private final NotificationService notificationService;
    private final NotificationSenderService notificationSenderService;

    public EmailScheduledService(UserService userService, NotificationService notificationService, NotificationSenderService notificationSenderService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.notificationSenderService = notificationSenderService;
    }

    @Scheduled(cron = "${notify.me.scheduled.email.cron:0 12 * * * 0}")
    public void sendQueuedEmails() {
        log.trace("Scheduled sending of all queued notifications started.");
        int page = 0;
        boolean hasNext;
        do {
            Page<User> userEntityPage = userService.getUsersPage(PageRequest.of(page, 100));
            page++;
            hasNext = userEntityPage.hasNext();
            userEntityPage.getContent().forEach(this::checkUser);
        } while (hasNext);
    }

    private void checkUser(User user) {
        if (user.getNotificationQueue().isEmpty()) {
            return;
        }
        Notification notification = collectNotifications(user);
        sendCollectedNotification(notification, user);
        userService.clearUserQueue(user);
    }

    private Notification collectNotifications(User user) {
        Map<NotificationType, List<Notification>> groupedNotifications = groupNotifications(user.getNotificationQueue());
        return Notification.builder()
            .type(NotificationType.WEEKLY_DIGEST)
            .userId(user.getId())
            .usedChannel(NotificationChannel.EMAIL)
            .creationDate(LocalDateTime.now())
            .urgency(NotificationUrgency.NORMAL)
            .title("Weekly email")
            .body(generateBody(groupedNotifications, user))
            .build();
    }

    private void sendCollectedNotification(Notification notification, User user) {
        Notification savedNot = notificationService.saveNotificationAndSendToInbox(notification, user);
        notificationSenderService.sendNotification(savedNot);
    }

    private Map<NotificationType, List<Notification>> groupNotifications(List<Notification> notificationQueue) {
        Map<NotificationType, List<Notification>> groupedNotifications = new HashMap<>();
        for (Notification notification : notificationQueue) {
            List<Notification> list = groupedNotifications.getOrDefault(notification.getType(), new ArrayList<>());
            list.add(notification);
            groupedNotifications.put(notification.getType(), list);
        }
        return groupedNotifications;
    }

    private String generateBody(Map<NotificationType, List<Notification>> groupedNotifications, User user) {
        String notificationBody = String.format("Hello %s %s,\n\nThe following things happened last week.", user.getFirstname(), user.getLastname());

        for (NotificationType key : groupedNotifications.keySet()) {
            String notificationType = "\n\n" + key + ": \n" + groupNotificationsOfTypes(groupedNotifications.get(key));
            notificationBody += notificationType;
        }
        return notificationBody;
    }

    private String groupNotificationsOfTypes(List<Notification> notifications) {
        String notificationsString = "";

        for (Notification notification : notifications) {
            notificationsString += notification.getBody() + "\n";
        }
        return notificationsString;
    }

}
