package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepo userRepo;
    private final UserPreferencesService userPreferencesService;

    public UserService(UserRepo userRepo, UserPreferencesService userPreferencesService) {
        this.userRepo = userRepo;
        this.userPreferencesService = userPreferencesService;
    }


    public User addNotificationToInbox(Notification notification, User user) {
        user.getInbox().add(notification);
        return save(user);
    }

    public Page<User> getUsersPage(PageRequest pageRequest) {
        return userRepo.findAll(pageRequest);
    }

    public User addNotificationToQueue(Notification notification) {
        User user = getUserById(notification.getUserId());
        user.getNotificationQueue().add(notification);
        return save(user);
    }

    private User getUserById(String userId) {
        Optional<User> userOptional = getById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found for id " + userId);
        }
        return userOptional.get();
    }

    public Optional<User> getById(String id) {
        return userRepo.findById(id);
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User setNotificationChannels(String userId, NotificationChannel normalChannel, NotificationChannel urgentChannel) {
        userPreferencesService.setNotificationChannels(getUserById(userId), normalChannel, urgentChannel);
        return getUserById(userId);
    }

    public User clearUserQueue(User user) {
        User toSave = User.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .userPreferences(user.getUserPreferences())
                .notificationQueue(new ArrayList<>())
                .inbox(user.getInbox())
                .build();
        return save(toSave);
    }
}
