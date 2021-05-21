package be.xplore.notify.me.services.scheduled;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import be.xplore.notify.me.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class LineStaffingScheduledServiceTest {

    @Autowired
    private LineStaffingScheduledService lineStaffingScheduledService;

    @MockBean
    private EventService eventService;
    @MockBean
    private EventLineService eventLineService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private NotificationSenderService notificationSenderService;

    @Autowired
    private Organization organization;
    @Autowired
    private Venue venue;
    @Autowired
    private Event event;
    @Autowired
    private User user;

    private List<Notification> sendNotifications;
    private List<EventLine> eventLines;

    @BeforeEach
    void setUp() {
        sendNotifications = new ArrayList<>();
        eventLines = new ArrayList<>();
        generateEventLines();

    }

    private void generateEventLines() {
        int i = 0;
        for (EventLineStatus status : EventLineStatus.values()) {
            i++;
            generateEventLine(i, status, new ArrayList<>());
        }
        List<User> users = new ArrayList<>();
        users.add(user);
        generateEventLine(++i, EventLineStatus.ASSIGNED, users);
    }

    private void generateEventLine(int i, EventLineStatus status, List<User> assignedUsers) {
        Line line = Line.builder().id("line-" + i).numberOfRequiredPeople(1).name("test").venue(venue).build();
        Organization toAssign = null;
        if (status == EventLineStatus.ASSIGNED) {
            toAssign = organization;
        }

        EventLine eventLine = EventLine.builder().line(line).organization(toAssign).event(event).lineManager(user).assignedUsers(assignedUsers).eventLineStatus(status).build();
        eventLines.add(eventLine);
    }

    @Test
    void checkLineStaffing() {
        setupMocks();
        lineStaffingScheduledService.checkLineStaffing();
        assertEquals(2, sendNotifications.size());
    }

    private void setupMocks() {
        mockSaveAndSendNotification();
        mockGetUpcomingEvents();
        mockGetEventLines();
    }

    private void mockGetEventLines() {
        given(eventLineService.getAllLinesOfEvent(any(), any(int.class))).will(i -> new PageImpl<>(eventLines));
    }

    private void mockGetUpcomingEvents() {
        given(eventService.getUpcomingEvents(any(int.class), any(int.class))).will(i -> new PageImpl<>(Collections.singletonList(event)));
    }

    private void mockSaveAndSendNotification() {
        given(notificationService.sendNotification(any(), any())).will(i -> {
            Notification sendNotification = i.getArgument(0);
            sendNotifications.add(sendNotification);
            return sendNotification;
        });
    }
}