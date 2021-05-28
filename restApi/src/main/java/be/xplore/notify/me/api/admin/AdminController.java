package be.xplore.notify.me.api.admin;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.dto.NotificationChannelAmountDto;
import be.xplore.notify.me.dto.NotificationDto;
import be.xplore.notify.me.dto.NotificationTypeDto;
import be.xplore.notify.me.mappers.NotificationDtoMapper;
import be.xplore.notify.me.services.notification.NotificationService;
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

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/admin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final NotificationService notificationService;
    private final NotificationDtoMapper notificationDtoMapper;

    public AdminController(NotificationService notificationService, NotificationDtoMapper notificationDtoMapper) {
        this.notificationService = notificationService;
        this.notificationDtoMapper = notificationDtoMapper;
    }

    @GetMapping("/notifications/event/{id}")
    public ResponseEntity<Page<NotificationDto>> getAllNotificationsByEventId(@PathVariable String id, @RequestParam int page) {
        Page<Notification> notifications = notificationService.getAllNotificationsByEventId(id, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(@RequestParam int page) {
        Page<Notification> notifications = notificationService.getAllNotifications(PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
    }

    @GetMapping("/notifications/type/{type}")
    public ResponseEntity<Page<NotificationDto>> getAllNotificationsByType(@RequestParam int page, @PathVariable NotificationType type) {
        Page<Notification> notifications = notificationService.getAllNotificationsByType(type, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
    }

    @GetMapping("/notifications/type/{type}/event/{id}")
    public ResponseEntity<Page<NotificationDto>> getAllNotificationsByEventAndType(@RequestParam int page, @PathVariable NotificationType type, @PathVariable String id) {
        Page<Notification> notifications = notificationService.getAllByTypeAndEvent(id, type, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notificationDtoMapper::toDto);
        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
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
    public ResponseEntity<List<String>> getAllEvents() {
        List<String> eventIds = notificationService.getAllEventIds();
        return new ResponseEntity<>(eventIds, HttpStatus.OK);
    }

}
