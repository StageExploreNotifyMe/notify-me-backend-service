package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.user.UserPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepo extends JpaRepository<UserPreferencesEntity, String> {
}
