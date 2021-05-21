package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.dto.event.EventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EventDtoMapperTest {

    @Autowired
    private EventDtoMapper mapper;

    @Autowired
    private Event object;

    @Test
    void toAndFromDto() {
        EventDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        Event fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }
}