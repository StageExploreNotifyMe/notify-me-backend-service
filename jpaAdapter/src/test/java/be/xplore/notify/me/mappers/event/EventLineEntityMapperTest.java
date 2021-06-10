package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.entity.event.EventLineEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EventLineEntityMapperTest {

    @Autowired
    private EventLineEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        EventLine object = EventLine.builder().id("500").assignedUsers(new ArrayList<>()).build();

        EventLineEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(Long.parseLong(object.getId()), entity.getId());

        EventLine fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}