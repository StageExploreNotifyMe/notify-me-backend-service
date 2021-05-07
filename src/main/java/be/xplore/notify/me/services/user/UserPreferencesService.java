package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.entity.mappers.user.UserPreferencesEntityMapper;
import be.xplore.notify.me.entity.user.UserPreferencesEntity;
import be.xplore.notify.me.repositories.UserPreferencesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserPreferencesService {
    private final UserPreferencesRepo userPreferencesRepo;
    private final UserPreferencesEntityMapper userPreferencesEntityMapper;

    public UserPreferencesService(UserPreferencesRepo userPreferencesRepo, UserPreferencesEntityMapper userPreferencesEntityMapper) {
        this.userPreferencesRepo = userPreferencesRepo;
        this.userPreferencesEntityMapper = userPreferencesEntityMapper;
    }

    public UserPreferences setNotificationChannels(User user, NotificationChannel normalChannel, NotificationChannel urgentChannel) {
        UserPreferences up = user.getUserPreferences();
        UserPreferences userPreferences = UserPreferences.builder().id(up.getId()).normalChannel(normalChannel).urgentChannel(urgentChannel).build();
        UserPreferencesEntity saved = userPreferencesRepo.save(userPreferencesEntityMapper.toEntity(userPreferences));
        return userPreferencesEntityMapper.fromEntity(saved);
    }
}
