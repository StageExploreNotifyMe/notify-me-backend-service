package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.user.UserDto;
import be.xplore.notify.me.mappers.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements DtoMapper<UserDto, User> {
    private final UserPreferencesDtoMapper userPreferencesDtoMapper;

    public UserDtoMapper(UserPreferencesDtoMapper userPreferencesDtoMapper) {
        this.userPreferencesDtoMapper = userPreferencesDtoMapper;
    }

    @Override
    public User fromDto(UserDto d) {
        return User.builder()
            .id(d.getId())
            .firstname(d.getFirstname())
            .lastname(d.getLastname())
            .userPreferences(userPreferencesDtoMapper.fromDto(d.getUserPreferences()))
            .roles(d.getRoles())
            .build();
    }

    @Override
    public UserDto toDto(User d) {
        return new UserDto(d.getId(), userPreferencesDtoMapper.toDto(d.getUserPreferences()), d.getFirstname(), d.getLastname(), d.getRoles());
    }
}
