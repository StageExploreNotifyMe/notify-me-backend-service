package be.xplore.notify.me.dto.venue;

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
public class VenueDto {
    private String id;
    private String name;
    private List<UserDto> venueManagers;

}
