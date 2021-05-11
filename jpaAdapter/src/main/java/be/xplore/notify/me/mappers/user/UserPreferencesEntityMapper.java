package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.entity.user.UserPreferencesEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesEntityMapper implements EntityMapper<UserPreferencesEntity, UserPreferences> {

    @Override
    public UserPreferences fromEntity(UserPreferencesEntity userPreferencesEntity) {
        return UserPreferences.builder()
                .id(userPreferencesEntity.getId())
                .normalChannel(userPreferencesEntity.getNormalChannel())
                .urgentChannel(userPreferencesEntity.getUrgentChannel())
                .build();
    }

    @Override
    public UserPreferencesEntity toEntity(UserPreferences userPreferences) {
        return new UserPreferencesEntity(userPreferences.getId(), userPreferences.getNormalChannel(), userPreferences.getUrgentChannel());
    }
}
