package be.xplore.notify.me.domain.user;

import be.xplore.notify.me.domain.notification.Notification;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class User {
    String id;
    UserPreferences userPreferences;
    String firstname;
    String lastname;
    String mobileNumber;
    String email;
    List<Notification> inbox;
    List<Notification> notificationQueue;
}
