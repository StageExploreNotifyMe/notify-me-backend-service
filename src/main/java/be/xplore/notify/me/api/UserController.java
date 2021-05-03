package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.dto.NotificationChannelDto;
import be.xplore.notify.me.dto.user.UserPreferencesDto;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationChannelDto> getUserPreferencesNotificationChannel() {
        NotificationChannelDto notificationChannels = new NotificationChannelDto();
        notificationChannels.setNotificationChannels(Arrays.asList(NotificationChannel.values()));
        return new ResponseEntity<>(notificationChannels, HttpStatus.OK);
    }

    @GetMapping("/{userId}/channel")
    public ResponseEntity<UserPreferences> getNormalChannelUser(@PathVariable String userId) {
        Optional<User> optionalUser = userService.getById(userId);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserPreferences userPreferences = optionalUser.get().getUserPreferences();
        return new ResponseEntity<>(userPreferences, HttpStatus.OK);
    }

    @PostMapping("/{userId}/preferences/channel")
    public ResponseEntity<Boolean> processChangeChannel(@RequestBody UserPreferencesDto userPreferencesDto, @PathVariable String userId) {
        userService.setNotificationChannels(userId, userPreferencesDto.getNormalChannel(), userPreferencesDto.getUrgentChannel());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
