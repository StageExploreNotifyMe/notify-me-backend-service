package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class EventNotificationServiceTest {

    @Autowired
    private EventNotificationService eventNotificationService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private EventLineService eventLineService;
    @MockBean
    private UserOrganizationService userOrganizationService;

    @Autowired
    private User user;
    @Autowired
    private UserOrganization userOrganization;
    @Autowired
    private EventLine eventLine;
    private EventLine eventLineWithAssignedUser;

    private List<Notification> sendNotifications;

    @BeforeEach
    void setUp() {
        sendNotifications = new ArrayList<>();
        List<User> assignedUsers = new ArrayList<>();
        assignedUsers.add(user);
        eventLineWithAssignedUser = EventLine.builder()
            .id(eventLine.getId() + "-withUsers")
            .eventLineStatus(eventLine.getEventLineStatus())
            .assignedUsers(assignedUsers)
            .lineManager(eventLine.getLineManager())
            .event(eventLine.getEvent())
            .line(eventLine.getLine())
            .organization(eventLine.getOrganization())
            .build();
    }

    @Test
    void eventCreated() {
        mockSaveNotificationToQueue();
        Event event = generateEventCreatedTestData();
        eventNotificationService.eventCreated(event);
        assertEquals(2, sendNotifications.size());
    }

    @Test
    void sendEventCanceledNotification() {
        mockGetEventLines();
        mockSaveNotificationToInbox();
        mockGetOrganizationLeaders();
        eventNotificationService.sendEventCanceledNotification(generateEventCreatedTestData());
        assertEquals(4, sendNotifications.size());
    }

    private void mockGetOrganizationLeaders() {
        given(userOrganizationService.getAllOrganizationLeadersByOrganizationId(any())).will(invocation -> {
            List<UserOrganization> users = new ArrayList<>();
            users.add(userOrganization);
            return users;
        });
    }

    private void mockGetEventLines() {
        given(eventLineService.getAllLinesOfEvent(any(), any(int.class))).will(i -> {
            List<EventLine> eventLines = new ArrayList<>();
            eventLines.add(eventLine);
            eventLines.add(eventLineWithAssignedUser);
            return new PageImpl<>(eventLines);
        });
    }

    private Event generateEventCreatedTestData() {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user);

        Venue venue = Venue.builder().name("Venue").lineManagers(users).build();
        return Event.builder().id("5").date(LocalDateTime.now()).name("test").venue(venue).build();
    }

    private void mockSaveNotificationToQueue() {
        given(notificationService.saveNotificationAndSendToQueue(any())).will(i -> {
            Notification not = i.getArgument(0);
            sendNotifications.add(not);
            return not;
        });
    }

    private void mockSaveNotificationToInbox() {
        given(notificationService.saveNotificationAndSendToInbox(any(), any())).will(i -> {
            Notification not = i.getArgument(0);
            sendNotifications.add(not);
            return not;
        });
    }
}