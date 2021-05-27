package be.xplore.notify.me.mappers.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NotificationEntityMapperTest {

    @Autowired
    private NotificationEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        Notification object = Notification.builder().id("ThisIsATest").build();

        NotificationEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(object.getId(), entity.getId());

        Notification fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}