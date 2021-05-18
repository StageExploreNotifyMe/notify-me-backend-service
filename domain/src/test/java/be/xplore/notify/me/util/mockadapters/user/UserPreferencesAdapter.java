package be.xplore.notify.me.util.mockadapters.user;

import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.persistence.UserPreferencesRepo;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesAdapter implements UserPreferencesRepo {

    @Override
    public UserPreferences save(UserPreferences userPreferences) {
        return null;
    }
}
