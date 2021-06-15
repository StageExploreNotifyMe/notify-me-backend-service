package be.xplore.notify.me.api.admin;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventIdsDto;
import be.xplore.notify.me.dto.notification.NotificationChannelAmountDto;
import be.xplore.notify.me.dto.notification.NotificationDto;
import be.xplore.notify.me.dto.notification.NotificationOverviewDto;
import be.xplore.notify.me.dto.notification.NotificationTypeDto;
import be.xplore.notify.me.mappers.NotificationDtoMapper;
import be.xplore.notify.me.mappers.event.EventDtoMapper;
import be.xplore.notify.me.mappers.user.UserDtoMapper;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/admin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final NotificationService notificationService;
    private final NotificationDtoMapper notificationDtoMapper;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;

    public AdminController(
            NotificationService notificationService,
            NotificationDtoMapper notificationDtoMapper,
            UserService userService, UserDtoMapper userDtoMapper,
            EventService eventService, EventDtoMapper eventDtoMapper
    ) {
        this.notificationService = notificationService;
        this.notificationDtoMapper = notificationDtoMapper;
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.eventService = eventService;
        this.eventDtoMapper = eventDtoMapper;
    }

    @GetMapping("/notifications/event/{id}")
    public ResponseEntity<NotificationOverviewDto> getAllNotificationsByEventId(@PathVariable String id, @RequestParam int page) {
        Page<Notification> notifications = notificationService.getAllNotificationsByEventId(id, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(generateNotificationOverviewDto(notificationDtos), HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<NotificationOverviewDto> getAllNotifications(@RequestParam int page) {
        Page<Notification> notifications = notificationService.getAllNotifications(PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(generateNotificationOverviewDto(notificationDtos), HttpStatus.OK);
    }

    @GetMapping("/notifications/type/{type}")
    public ResponseEntity<NotificationOverviewDto> getAllNotificationsByType(@RequestParam int page, @PathVariable NotificationType type) {
        Page<Notification> notifications = notificationService.getAllNotificationsByType(type, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(generateNotificationOverviewDto(notificationDtos), HttpStatus.OK);
    }

    @GetMapping("/notifications/type/{type}/event/{id}")
    public ResponseEntity<NotificationOverviewDto> getAllNotificationsByEventAndType(@RequestParam int page, @PathVariable NotificationType type, @PathVariable String id) {
        Page<Notification> notifications = notificationService.getAllByTypeAndEvent(id, type, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(generateNotificationOverviewDto(notificationDtos), HttpStatus.OK);
    }

    @GetMapping("/notificationTypes")
    public ResponseEntity<NotificationTypeDto> getAllNotificationTypes() {
        NotificationTypeDto notificationTypes = new NotificationTypeDto();
        notificationTypes.setNotificationTypes(Arrays.asList(NotificationType.values()));
        return new ResponseEntity<>(notificationTypes, HttpStatus.OK);
    }

    @GetMapping("/channelAmount")
    public ResponseEntity<NotificationChannelAmountDto> getAmountOfNotificationChannels() {
        NotificationChannelAmountDto channelAmountDto = new NotificationChannelAmountDto();
        channelAmountDto.setNotificationAmounts(notificationService.getChannelAmount());
        return new ResponseEntity<>(channelAmountDto, HttpStatus.OK);
    }

    @GetMapping("/eventId")
    public ResponseEntity<EventIdsDto> getAllEvents() {
        List<String> eventIds = notificationService.getAllEventIds();
        return new ResponseEntity<>(new EventIdsDto(eventIds), HttpStatus.OK);
    }

    private NotificationOverviewDto generateNotificationOverviewDto(Page<NotificationDto> notificationDtos) {
        Set<String> eventIds = notificationDtos.map(NotificationDto::getEventId).stream().collect(Collectors.toSet());
        List<Event> events = eventService.getAllById(new ArrayList<>(eventIds));
        Set<String> userIds = notificationDtos.map(NotificationDto::getUserId).stream().collect(Collectors.toSet());
        List<User> users = userService.getAllById(new ArrayList<>(userIds));

        return new NotificationOverviewDto(
            notificationDtos,
            users.stream().map(userDtoMapper::toDto).collect(Collectors.toList()),
            events.stream().map(eventDtoMapper::toDto).collect(Collectors.toList())
        );
    }

}
