package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.dto.event.EventLineDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EventLineDtoMapperTest {

    @Autowired
    private EventLineDtoMapper mapper;

    @Autowired
    private EventLine object;

    @Test
    void toAndFromDto() {
        EventLineDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        EventLine fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }
}