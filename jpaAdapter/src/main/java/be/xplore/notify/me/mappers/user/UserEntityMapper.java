package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.entity.user.AuthenticationCodeEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper implements EntityMapper<UserEntity, User> {
    private final UserPreferencesEntityMapper userPreferencesEntityMapper;
    private final NotificationEntityMapper notificationEntityMapper;
    private final AuthenticationMapper authenticationMapper;

    public UserEntityMapper(UserPreferencesEntityMapper userPreferencesEntityMapper, NotificationEntityMapper notificationEntityMapper, AuthenticationMapper authenticationMapper) {
        this.userPreferencesEntityMapper = userPreferencesEntityMapper;
        this.notificationEntityMapper = notificationEntityMapper;
        this.authenticationMapper = authenticationMapper;
    }

    @Override
    public User fromEntity(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return User.builder()
                .id(String.valueOf(userEntity.getId()))
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .userPreferences(userPreferencesEntityMapper.fromEntity(userEntity.getUserPreferences()))
                .inbox(getNotifications(userEntity))
                .notificationQueue(getNotificationQueue(userEntity))
                .mobileNumber(userEntity.getMobileNumber())
                .email(userEntity.getEmail())
                .passwordHash(userEntity.getPasswordHash())
                .authenticationCodes(getAuthenticationCodes(userEntity))
                .roles(userEntity.getRoles())
                .registrationStatus(userEntity.getRegistrationStatus())
                .build();
    }

    private List<Notification> getNotificationQueue(UserEntity userEntity) {
        List<Notification> notificationQueue = new ArrayList<>();
        if (userEntity.getNotificationQueue() != null) {
            notificationQueue = userEntity.getNotificationQueue().stream().map(notificationEntityMapper::fromEntity).collect(Collectors.toList());
        }
        return notificationQueue;
    }

    private List<Notification> getNotifications(UserEntity userEntity) {
        List<Notification> inbox = new ArrayList<>();
        if (userEntity.getInbox() != null) {
            inbox = userEntity.getInbox().stream().map(notificationEntityMapper::fromEntity).collect(Collectors.toList());
        }
        return inbox;
    }

    private List<AuthenticationCode> getAuthenticationCodes(UserEntity userEntity) {
        List<AuthenticationCode> authenticationCodes = new ArrayList<>();
        if (userEntity.getAuthenticationCodes() != null) {
            authenticationCodes = userEntity.getAuthenticationCodes().stream().map(authenticationMapper::fromEntity).collect(Collectors.toList());
        }
        return authenticationCodes;
    }

    @Override
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return new UserEntity(LongParser.parseLong(user.getId()), userPreferencesEntityMapper.toEntity(
            user.getUserPreferences()),
            user.getFirstname(),
            user.getLastname(),
            user.getMobileNumber(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getRegistrationStatus(),
            getNotificationEntities(user),
            getNotificationQueueEntities(user),
            getAuthenticationCodeEntities(user),
            user.getRoles()
        );
    }

    private List<NotificationEntity> getNotificationQueueEntities(User user) {
        List<NotificationEntity> notificationQueue = new ArrayList<>();
        if (user.getNotificationQueue() != null) {
            notificationQueue = user.getNotificationQueue().stream().map(notificationEntityMapper::toEntity).collect(Collectors.toList());
        }
        return notificationQueue;
    }

    private List<NotificationEntity> getNotificationEntities(User user) {
        List<NotificationEntity> inbox = new ArrayList<>();
        if (user.getInbox() != null) {
            inbox = user.getInbox().stream().map(notificationEntityMapper::toEntity).collect(Collectors.toList());
        }
        return inbox;
    }

    private List<AuthenticationCodeEntity> getAuthenticationCodeEntities(User user) {
        List<AuthenticationCodeEntity> authenticationCodes = new ArrayList<>();
        if (user.getAuthenticationCodes() != null) {
            authenticationCodes = user.getAuthenticationCodes().stream().map(authenticationMapper::toEntity).collect(Collectors.toList());
        }
        return authenticationCodes;
    }
}
