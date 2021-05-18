package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;

public interface NotificationSenderService {

    void sendNotification(Notification notification);

}
