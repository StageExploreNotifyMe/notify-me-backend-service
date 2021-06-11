package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.entity.user.UserPreferencesEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesEntityMapper implements EntityMapper<UserPreferencesEntity, UserPreferences> {

    @Override
    public UserPreferences fromEntity(UserPreferencesEntity userPreferencesEntity) {
        if (userPreferencesEntity == null) {
            return null;
        }
        return UserPreferences.builder()
                .id(String.valueOf(userPreferencesEntity.getId()))
                .normalChannel(userPreferencesEntity.getNormalChannel())
                .urgentChannel(userPreferencesEntity.getUrgentChannel())
                .build();
    }

    @Override
    public UserPreferencesEntity toEntity(UserPreferences userPreferences) {
        if (userPreferences == null) {
            return null;
        }
        return new UserPreferencesEntity(LongParser.parseLong(userPreferences.getId()), userPreferences.getNormalChannel(), userPreferences.getUrgentChannel());
    }
}
