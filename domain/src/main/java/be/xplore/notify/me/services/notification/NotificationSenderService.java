package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;

public interface NotificationSenderService {
    Notification sendNotification(Notification notification, User user);
}
