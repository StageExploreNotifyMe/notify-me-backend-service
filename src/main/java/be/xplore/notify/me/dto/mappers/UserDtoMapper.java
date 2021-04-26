package be.xplore.notify.me.dto.mappers;

import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements DtoMapper<UserDto, User> {
    @Override
    public User fromDto(UserDto d) {
        return User.builder().id(d.getId()).build();
    }

    @Override
    public UserDto toDto(User d) {
        return new UserDto(d.getId());
    }
}
