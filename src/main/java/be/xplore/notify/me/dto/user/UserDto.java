package be.xplore.notify.me.dto.user;

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
    private UserPreferencesDto userPreferences;
    private String firstname;
    private String lastname;
}
