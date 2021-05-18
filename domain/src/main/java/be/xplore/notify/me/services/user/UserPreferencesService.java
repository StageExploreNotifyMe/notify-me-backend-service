package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.persistence.UserPreferencesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserPreferencesService {
    private final UserPreferencesRepo userPreferencesRepo;

    public UserPreferencesService(UserPreferencesRepo userPreferencesRepo) {
        this.userPreferencesRepo = userPreferencesRepo;
    }

    public UserPreferences setNotificationChannels(User user, NotificationChannel normalChannel, NotificationChannel urgentChannel) {
        UserPreferences up = user.getUserPreferences();
        UserPreferences userPreferences = UserPreferences.builder().id(up.getId()).normalChannel(normalChannel).urgentChannel(urgentChannel).build();
        return userPreferencesRepo.save(userPreferences);
    }
}
