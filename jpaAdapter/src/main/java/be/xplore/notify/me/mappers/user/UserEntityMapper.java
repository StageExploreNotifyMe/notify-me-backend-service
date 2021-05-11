package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.entity.user.UserEntity;
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
        List<Notification> notificationQueue = new ArrayList<>();
        if (userEntity.getNotificationQueue() != null) {
            notificationQueue = userEntity.getNotificationQueue().stream().map(notificationEntityMapper::fromEntity).collect(Collectors.toList());
        }
        return User.builder()
            .id(userEntity.getId())
            .firstname(userEntity.getFirstname())
            .lastname(userEntity.getLastname())
            .userPreferences(userPreferencesEntityMapper.fromEntity(userEntity.getUserPreferences()))
            .inbox(userEntity.getInbox().stream().map(notificationEntityMapper::fromEntity).collect(Collectors.toList()))
            .notificationQueue(notificationQueue)
            .build();
    }

    @Override
    public UserEntity toEntity(User user) {
        List<NotificationEntity> notificationQueue = new ArrayList<>();
        if (user.getNotificationQueue() != null) {
            notificationQueue = user.getNotificationQueue().stream().map(notificationEntityMapper::toEntity).collect(Collectors.toList());
        }

        return new UserEntity(user.getId(), userPreferencesEntityMapper.toEntity(
            user.getUserPreferences()),
            user.getFirstname(),
            user.getLastname(),
            user.getInbox().stream().map(notificationEntityMapper::toEntity).collect(Collectors.toList()),
            notificationQueue
        );
    }
}
