package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.repositories.UserRepo;
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
    private final UserEntityMapper userEntityMapper;

    public UserService(UserRepo userRepo, UserPreferencesService userPreferencesService, UserEntityMapper userEntityMapper) {
        this.userRepo = userRepo;
        this.userPreferencesService = userPreferencesService;
        this.userEntityMapper = userEntityMapper;
    }

    public User addNotificationToInbox(Notification notification) {
        User user = getUserById(notification.getUserId());
        user.getInbox().add(notification);
        return save(user);
    }

    public Page<User> getUsersPage(PageRequest pageRequest) {
        Page<UserEntity> userEntityPage = userRepo.findAll(pageRequest);
        return userEntityPage.map(userEntityMapper::fromEntity);
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
        Optional<UserEntity> optional = userRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        User user = userEntityMapper.fromEntity(optional.get());
        return Optional.of(user);
    }

    public User save(User user) {
        UserEntity userEntity = userRepo.save(userEntityMapper.toEntity(user));
        return userEntityMapper.fromEntity(userEntity);
    }

    public User setNotificationChannels(String userId, NotificationChannel normalChannel, NotificationChannel urgentChannel) {
        Optional<User> optionalUser = getOptionalUser(userId);
        User user = optionalUser.get();
        userPreferencesService.setNotificationChannels(user, normalChannel, urgentChannel);
        Optional<User> optional = getOptionalUser(user.getId());
        return optional.get();

    }

    private Optional<User> getOptionalUser(String userId) {
        Optional<User> optionalUser = getById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user with id: " + userId + "found");
        }
        return optionalUser;
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
