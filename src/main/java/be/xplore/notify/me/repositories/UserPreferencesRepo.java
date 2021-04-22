package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.user.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepo extends JpaRepository<UserPreferences, String> {
}
