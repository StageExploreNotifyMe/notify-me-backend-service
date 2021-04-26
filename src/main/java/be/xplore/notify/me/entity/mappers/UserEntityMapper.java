package be.xplore.notify.me.entity.mappers;

import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper implements EntityMapper<UserEntity, User> {
    @Override
    public User fromEntity(UserEntity userEntity) {
        return User.builder().id(userEntity.getId()).build();
    }

    @Override
    public UserEntity toEntity(User user) {
        return new UserEntity(user.getId());
    }
}
