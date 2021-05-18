package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.user.UserPreferences;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepo {
    UserPreferences save(UserPreferences userPreferences);
}
