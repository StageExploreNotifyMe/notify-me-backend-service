package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.dto.user.UserPreferencesDto;
import be.xplore.notify.me.mappers.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesDtoMapper implements DtoMapper<UserPreferencesDto, UserPreferences> {
    @Override
    public UserPreferences fromDto(UserPreferencesDto d) {
        return UserPreferences.builder().id(d.getId()).normalChannel(d.getNormalChannel()).urgentChannel(d.getUrgentChannel()).build();
    }

    @Override
    public UserPreferencesDto toDto(UserPreferences d) {
        return new UserPreferencesDto(d.getId(), d.getNormalChannel(), d.getUrgentChannel());
    }
}
