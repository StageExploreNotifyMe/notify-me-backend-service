package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.dto.venue.VenueDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class VenueDtoMapperTest {

    @Autowired
    private VenueDtoMapper mapper;

    @Autowired
    private Venue object;

    @Test
    void toAndFromDto() {
        VenueDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        Venue fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }
}