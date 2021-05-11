package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.entity.user.UserPreferencesEntity;
import be.xplore.notify.me.mappers.user.UserPreferencesEntityMapper;
import be.xplore.notify.me.persistence.UserPreferencesRepo;
import be.xplore.notify.me.repositories.JpaUserPreferencesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserPreferencesAdapter implements UserPreferencesRepo {
    private final JpaUserPreferencesRepo jpaUserPreferencesRepo;
    private final UserPreferencesEntityMapper userPreferencesEntityMapper;

    public UserPreferencesAdapter(JpaUserPreferencesRepo jpaUserPreferencesRepo, UserPreferencesEntityMapper userPreferencesEntityMapper) {
        this.jpaUserPreferencesRepo = jpaUserPreferencesRepo;
        this.userPreferencesEntityMapper = userPreferencesEntityMapper;
    }

    @Override
    public UserPreferences save(UserPreferences preferences) {
        UserPreferencesEntity preferencesEntity = jpaUserPreferencesRepo.save(userPreferencesEntityMapper.toEntity(preferences));
        return userPreferencesEntityMapper.fromEntity(preferencesEntity);
    }
}
