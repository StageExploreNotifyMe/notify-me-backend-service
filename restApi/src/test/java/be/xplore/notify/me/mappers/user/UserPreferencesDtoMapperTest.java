package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.dto.user.UserPreferencesDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserPreferencesDtoMapperTest {

    @Autowired
    private UserPreferencesDtoMapper mapper;

    @Autowired
    private User object;

    @Test
    void toAndFromDto() {
        UserPreferencesDto dto = mapper.toDto(object.getUserPreferences());
        assertNotNull(dto);
        assertEquals(object.getUserPreferences().getId(), dto.getId());

        UserPreferences fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getUserPreferences().getId());
    }
}