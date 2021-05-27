package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.dto.line.LineDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LineDtoMapperTest {

    @Autowired
    private LineDtoMapper mapper;

    @Autowired
    private Line object;

    @Test
    void toAndFromDto() {
        LineDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        Line fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }
}