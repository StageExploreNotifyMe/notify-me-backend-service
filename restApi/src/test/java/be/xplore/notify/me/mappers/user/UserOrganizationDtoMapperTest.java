package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.user.UserOrganizationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserOrganizationDtoMapperTest {

    @Autowired
    private UserOrganizationDtoMapper mapper;

    @Autowired
    private UserOrganization object;

    @Test
    void toAndFromDto() {
        UserOrganizationDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        UserOrganization fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }
}