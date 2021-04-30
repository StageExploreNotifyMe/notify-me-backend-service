package be.xplore.notify.me.util;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.domain.user.UserPreferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Configuration
public class Testdata {

    @Bean
    User testUser() {
        UserPreferences userPreferences = UserPreferences.builder().id("1")
                .normalChannel(NotificationChannel.EMAIL).urgentChannel(NotificationChannel.SMS).build();
        return User.builder().id("1").userPreferences(userPreferences).firstname("John").lastname("Doe").inbox(new ArrayList<>()).build();
    }

    @Bean
    Organization testOrganization() {
        return Organization.builder().id("1").name("Test Organization").build();
    }

    @Bean
    Notification testNotification() {
        return Notification.builder().id("1").userId(testUser().getId()).title("Test").body("This is a test")
                .usedChannel(NotificationChannel.EMAIL).type(NotificationType.USER_JOINED).urgency(NotificationUrgency.NORMAL)
                .build();
    }

    @Bean
    UserOrganization testUserOrganization() {
        return UserOrganization.builder().id("1").user(testUser()).organization(testOrganization())
                .role(Role.MEMBER).status(MemberRequestStatus.PENDING).build();
    }

    @Bean
    Venue testVenue() {
        return Venue.builder().id("1").name("Test Venue").build();
    }

    @Bean
    Event testEvent() {
        return Event.builder().id("1").venue(testVenue()).name("test").eventStatus(EventStatus.CREATED).date(LocalDateTime.now().plusMonths(2)).build();
    }

}
