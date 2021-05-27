package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserEntityMapperTest {

    @Autowired
    private UserEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        User object = User.builder().id("ThisIsATest").inbox(new ArrayList<>()).notificationQueue(new ArrayList<>()).build();

        UserEntity entity = mapper.toEntity(object);
        doEntityAsserts(object, entity);

        User fromEntity = mapper.fromEntity(entity);
        doObjectAsserts(object, fromEntity);
    }

    @Test
    void toAndFromEntityWithNulls() {
        User object = User.builder().id("ThisIsATest").inbox(null).notificationQueue(null).build();

        UserEntity entity = mapper.toEntity(object);
        doEntityAsserts(object, entity);

        entity.setInbox(null);
        entity.setNotificationQueue(null);
        User fromEntity = mapper.fromEntity(entity);
        doObjectAsserts(object, fromEntity);
    }

    private void doEntityAsserts(User object, UserEntity entity) {
        assertNotNull(entity);
        assertEquals(object.getId(), entity.getId());
    }

    private void doObjectAsserts(User object, User fromEntity) {
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}