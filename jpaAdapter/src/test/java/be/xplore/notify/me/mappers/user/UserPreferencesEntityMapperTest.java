package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.entity.user.UserPreferencesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserPreferencesEntityMapperTest {

    @Autowired
    private UserPreferencesEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        UserPreferences object = UserPreferences.builder().id("500").build();

        UserPreferencesEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(Long.parseLong(object.getId()), entity.getId());

        UserPreferences fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}