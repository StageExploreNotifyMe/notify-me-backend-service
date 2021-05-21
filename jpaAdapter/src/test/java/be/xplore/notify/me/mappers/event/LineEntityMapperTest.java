package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.entity.event.LineEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LineEntityMapperTest {

    @Autowired
    private LineEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        Line object = Line.builder().id("ThisIsATest").build();

        LineEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(object.getId(), entity.getId());

        Line fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}