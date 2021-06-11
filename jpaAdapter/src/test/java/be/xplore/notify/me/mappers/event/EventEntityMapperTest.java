package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.entity.event.EventEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EventEntityMapperTest {

    @Autowired
    private EventEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        Event object = Event.builder().id("500").build();

        EventEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(Long.parseLong(object.getId()), entity.getId());

        Event fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}