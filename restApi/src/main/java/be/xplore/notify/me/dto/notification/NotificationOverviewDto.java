package be.xplore.notify.me.dto.notification;

import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationOverviewDto {
    private Page<NotificationDto> notificationDtoPage;
    private List<UserDto> userDtos;
    private List<EventDto> eventDtos;
}
