package be.xplore.notify.me.domain.user;

import be.xplore.notify.me.domain.notification.Notification;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@Builder
public class User {
    String id;
    UserPreferences userPreferences;
    String firstname;
    String lastname;
    String mobileNumber;
    String email;
    String passwordHash;
    RegistrationStatus registrationStatus;
    List<Notification> inbox;
    List<Notification> notificationQueue;
    List<AuthenticationCode> authenticationCodes;
    Set<Role> roles;
}
