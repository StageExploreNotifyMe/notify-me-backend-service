package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.dto.notification.NotificationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NotificationDtoMapperTest {

    @Autowired
    private NotificationDtoMapper mapper;

    @Autowired
    private Notification object;

    @Test
    void toAndFromDto() {
        NotificationDto dto = mapper.toDto(object);
        assertNotNull(dto);
        assertEquals(object.getId(), dto.getId());

        Notification fromDto = mapper.fromDto(dto);
        assertNotNull(fromDto);
        assertEquals(fromDto.getId(), object.getId());
    }
}