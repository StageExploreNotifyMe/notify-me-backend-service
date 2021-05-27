package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.dto.organization.OrganizationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class OrganizationDtoMapperTest {

    @Autowired
    private OrganizationDtoMapper mapper;

    @Autowired
    private Organization object;

    @Test
    void toAndFromDto() {
        OrganizationDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        Organization fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }

    @Test
    void toAndFromDtoWithNull() {
        OrganizationDto dto = mapper.toDto(null);
        assertNull(dto);

        Organization fromDto = mapper.fromDto(dto);
        assertNull(fromDto);
    }
}