package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.dto.NotificationDto;
import be.xplore.notify.me.services.notification.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    public NotificationController(NotificationService notificationService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/inbox/{userId}/pending/{page}")
    public ResponseEntity<Page<NotificationDto>> getUserNotification(@PathVariable String userId, @PathVariable int page) {
        Page<Notification> notifications = notificationService.getAllNotifications(userId, PageRequest.of(page, 20));
        Page<NotificationDto> notificationDtos = notifications.map(notification -> modelMapper.map(notification, NotificationDto.class));
        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
    }

}
