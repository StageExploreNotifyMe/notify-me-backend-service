package be.xplore.notify.me.services.scheduled;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.notification.NotificationUrgency;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LineStatusScheduledService {

    private final VenueService venueService;
    private final EventLineService eventLineService;
    private final NotificationService notificationService;

    public LineStatusScheduledService(
            VenueService venueService,
            EventLineService eventLineService,
            NotificationService notificationService
    ) {
        this.venueService = venueService;
        this.eventLineService = eventLineService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${notify.me.scheduled.email.cron:0 12 * * * 0}")
    public void sendStaffingOverviewEmail() {
        log.trace("Scheduled sending of staffing overview email for line managers started.");
        int page = 0;
        boolean hasNext;
        do {
            Page<Venue> venuesPage = venueService.getAllVenues(page);
            page++;
            hasNext = venuesPage.hasNext();
            venuesPage.getContent().forEach(venue -> venue.getLineManagers().forEach(user -> sendLineManagerEmail(user, venue)));
        } while (hasNext);
    }

    private void sendLineManagerEmail(User user, Venue venue) {
        Map<Event, List<EventLine>> groupedEventLines = getGroupedEventLines(user);
        if (groupedEventLines.keySet().size() != 0) {
            generateAndSendNotification(user, venue, groupedEventLines);
        }
    }

    private void generateAndSendNotification(User user, Venue venue, Map<Event, List<EventLine>> groupedEventLines) {
        Notification notification = Notification.builder()
                .type(NotificationType.WEEKLY_DIGEST)
                .userId(user.getId())
                .usedChannel(NotificationChannel.EMAIL)
                .creationDate(LocalDateTime.now())
                .urgency(NotificationUrgency.NORMAL)
                .title("Weekly line staffing email")
                .body(generateLineMangerNotificationBody(user, venue, groupedEventLines))
                .build();
        notificationService.sendNotification(notification, user);
    }

    private String generateLineMangerNotificationBody(User user, Venue venue, Map<Event, List<EventLine>> groupedEventLines) {
        String body = String.format(
                "Hi %s %s\n\nThis is an overview of the staffing of all the lines of all the events at venue %s",
                user.getFirstname(),
                user.getLastname(),
                venue.getName()
        );
        for (Event key : groupedEventLines.keySet()) {
            body += String.format("\n\nLines for Event %s:\n", key.getName());
            body += generateEventBodyString(groupedEventLines.get(key));
        }
        return body;
    }

    private String generateEventBodyString(List<EventLine> eventLines) {
        String body = "";
        for (EventLine eventLine : eventLines) {
            float assignedUsers = eventLine.getAssignedUsers().size();
            float requiredUsers = eventLine.getLine().getNumberOfRequiredPeople();
            int percentage = Math.round((assignedUsers / requiredUsers) * 100);
            body += String.format("\n%s: %s (%s/%s)", eventLine.getLine().getName(), percentage, assignedUsers, requiredUsers);
        }
        return body;
    }

    private Map<Event, List<EventLine>> getGroupedEventLines(User user) {
        List<EventLine> allActiveEventLinesOfLineManager = eventLineService.getAllActiveEventLinesOfLineManager(user);
        Map<Event, List<EventLine>> groupedEventLines = new HashMap<>();
        for (EventLine eventLine : allActiveEventLinesOfLineManager) {
            List<EventLine> eventEventLines = groupedEventLines.getOrDefault(eventLine.getEvent(), new ArrayList<>());
            eventEventLines.add(eventLine);
            groupedEventLines.put(eventLine.getEvent(), eventEventLines);
        }
        return groupedEventLines;
    }

}
