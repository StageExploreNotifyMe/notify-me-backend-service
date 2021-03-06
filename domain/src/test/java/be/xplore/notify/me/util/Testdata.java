package be.xplore.notify.me.util;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.RegistrationStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.domain.user.UserPreferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Configuration
public class Testdata {

    @Bean
    User testUser() {
        UserPreferences userPreferences = UserPreferences.builder().id("1")
                .normalChannel(NotificationChannel.EMAIL).urgentChannel(NotificationChannel.SMS).build();
        return User.builder().id("1").userPreferences(userPreferences).firstname("John").lastname("Doe").registrationStatus(RegistrationStatus.OK)
             .inbox(new ArrayList<>()).notificationQueue(new ArrayList<>()).roles(new HashSet<>()).authenticationCodes(new ArrayList<>()).build();
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
        return Venue.builder().id("1").name("Test Venue").venueManagers(new ArrayList<>()).build();
    }

    @Bean
    Event testEvent() {
        return Event.builder().id("1").venue(testVenue()).name("test").eventStatus(EventStatus.CREATED).date(LocalDateTime.now().plusMonths(2)).build();
    }

    @Bean
    Line testLine() {
        return Line.builder().id("1").name("test").description("this is a test line").venue(testVenue()).build();
    }

    @Bean
    EventLine testEventLine() {
        return EventLine.builder()
            .id("1").line(testLine())
            .assignedUsers(new ArrayList<>())
            .organization(testOrganization())
            .event(testEvent())
            .eventLineStatus(EventLineStatus.CREATED)
            .lineManager(testUser())
            .build();
    }

    @Bean
    List<AuthenticationCode> testAuthenticationCodes() {
        AuthenticationCode authenticationCode = AuthenticationCode.builder()
                .id("1")
                .code("2525")
                .creationDate(LocalDateTime.now())
                .notificationChannel(NotificationChannel.EMAIL)
                .build();
        List<AuthenticationCode> authenticationCodes = new ArrayList<>();
        authenticationCodes.add(authenticationCode);
        return authenticationCodes;
    }

}
