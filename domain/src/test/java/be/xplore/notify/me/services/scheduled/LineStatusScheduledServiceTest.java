package be.xplore.notify.me.services.scheduled;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class LineStatusScheduledServiceTest {

    @Autowired
    private LineStatusScheduledService lineStatusScheduledService;

    @MockBean
    private VenueService venueService;
    @MockBean
    private EventLineService eventLineService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private NotificationSenderService notificationSenderService;

    private List<Notification> sendNotifications;
    private List<Venue> venues;
    private List<User> lineManagers;
    private List<EventLine> eventLines;

    @Autowired
    private EventLine eventLine;

    @BeforeEach
    void setUp() {
        sendNotifications = new ArrayList<>();
        eventLines = new ArrayList<>();
        createTestUsers();
    }

    @Test
    void sendStaffingOverviewEmail() {
        venues = new ArrayList<>();
        eventLines.add(eventLine);
        venues.add(createVenue("1", lineManagers.subList(0, lineManagers.size() / 2)));
        venues.add(createVenue("2", lineManagers.subList(lineManagers.size() / 2, lineManagers.size())));
        mockEverything();
        lineStatusScheduledService.sendStaffingOverviewEmail();
        assertEquals(10, sendNotifications.size());
    }

    @Test
    void sendStaffingOverviewEmailNoEvents() {
        venues = new ArrayList<>();
        venues.add(createVenue("1", lineManagers.subList(0, lineManagers.size() / 2)));
        venues.add(createVenue("2", lineManagers.subList(lineManagers.size() / 2, lineManagers.size())));
        mockEverything();
        lineStatusScheduledService.sendStaffingOverviewEmail();
        assertEquals(0, sendNotifications.size());
    }

    private void createTestUsers() {
        lineManagers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lineManagers.add(User.builder()
                    .firstname("Test")
                    .lastname("user")
                    .id("user-" + i)
                    .build());
        }
    }

    private void mockEverything() {
        given(venueService.getAllVenues(any(int.class))).will(i -> new PageImpl<>(venues));
        given(eventLineService.getAllActiveEventLinesOfLineManager(any())).will(i -> eventLines);
        given(notificationService.sendNotification(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            sendNotifications.add(n);
            return n;
        });
    }

    private Venue createVenue(String id, List<User> lineManagers) {
        return Venue.builder()
            .name("Venue-" + id)
            .id(id)
            .lineManagers(lineManagers)
            .venueManagers(new ArrayList<>())
            .build();
    }
}