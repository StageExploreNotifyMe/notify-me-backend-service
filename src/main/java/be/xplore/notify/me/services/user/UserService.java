package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        Optional<User> userOptional = getById(notification.getUserId());
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found for id " + notification.getUserId());
        }
        User user = userOptional.get();
        user.getInbox().add(notification);
        return save(user);
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
}
