package be.xplore.notify.me.dto.user;

import be.xplore.notify.me.domain.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private UserPreferencesDto userPreferences;
    private String firstname;
    private String lastname;
    private Set<Role> roles;
}
