package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.mappers.notification.NotificationEntityMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper implements EntityMapper<UserEntity, User> {
    private final UserPreferencesEntityMapper userPreferencesEntityMapper;
    private final NotificationEntityMapper notificationEntityMapper;

    public UserEntityMapper(UserPreferencesEntityMapper userPreferencesEntityMapper, NotificationEntityMapper notificationEntityMapper) {
        this.userPreferencesEntityMapper = userPreferencesEntityMapper;
        this.notificationEntityMapper = notificationEntityMapper;
    }

    @Override
    public User fromEntity(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        List<Notification> notificationQueue = new ArrayList<>();
        if (userEntity.getNotificationQueue() != null) {
            notificationQueue = userEntity.getNotificationQueue().stream().map(notificationEntityMapper::fromEntity).collect(Collectors.toList());
        }
        List<Notification> inbox = new ArrayList<>();
        if (userEntity.getInbox() != null) {
            inbox = userEntity.getInbox().stream().map(notificationEntityMapper::fromEntity).collect(Collectors.toList());
        }

        return User.builder()
            .id(userEntity.getId())
            .firstname(userEntity.getFirstname())
            .lastname(userEntity.getLastname())
            .userPreferences(userPreferencesEntityMapper.fromEntity(userEntity.getUserPreferences()))
            .inbox(inbox)
            .notificationQueue(notificationQueue)
            .mobileNumber(userEntity.getMobileNumber())
            .email(userEntity.getEmail())
            .passwordHash(userEntity.getPasswordHash())
            .roles(userEntity.getRoles())
            .build();
    }

    @Override
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        List<NotificationEntity> notificationQueue = new ArrayList<>();
        if (user.getNotificationQueue() != null) {
            notificationQueue = user.getNotificationQueue().stream().map(notificationEntityMapper::toEntity).collect(Collectors.toList());
        }

        List<NotificationEntity> inbox = new ArrayList<>();
        if (user.getInbox() != null) {
            inbox = user.getInbox().stream().map(notificationEntityMapper::toEntity).collect(Collectors.toList());
        }

        return new UserEntity(user.getId(), userPreferencesEntityMapper.toEntity(
            user.getUserPreferences()),
            user.getFirstname(),
            user.getLastname(),
            user.getMobileNumber(),
            user.getEmail(),
            user.getPasswordHash(),
            inbox,
            notificationQueue,
            user.getRoles()
        );
    }
}
