package be.xplore.notify.me.api.dto;

import be.xplore.notify.me.domain.user.UserPreferences;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private UserPreferences userPreferences;
    private String firstname;
    private String lastname;
}
