package be.xplore.notify.me.dto.event;

import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.dto.OrganizationDto;
import be.xplore.notify.me.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventLineDto {
    private String id;
    private LineDto line;
    private EventDto event;
    private OrganizationDto organization;
    private List<UserDto> assignedUsers;
    private EventLineStatus eventLineStatus;
}

