package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.mappers.user.UserEntityMapper;
import be.xplore.notify.me.persistence.UserRepo;
import be.xplore.notify.me.repositories.JpaUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.Optional;

@Slf4j
@Component
@Transactional
public class UserAdapter implements UserRepo {
    private final JpaUserRepo jpaUserRepo;
    private final UserEntityMapper userEntityMapper;

    public UserAdapter(JpaUserRepo jpaUserRepo, UserEntityMapper userEntityMapper) {
        this.jpaUserRepo = jpaUserRepo;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public Optional<User> findById(String id) {
        return mapToObject(jpaUserRepo.findById(id));
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = jpaUserRepo.save(userEntityMapper.toEntity(user));
        return userEntityMapper.fromEntity(userEntity);
    }

    @Override

    public Page<User> findAll(PageRequest pageRequest) {
        Page<UserEntity> entityPage = jpaUserRepo.findAll(pageRequest);
        return entityPage.map(userEntityMapper::fromEntity);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return mapToObject(jpaUserRepo.findByEmail(email));
    }

    private Optional<User> mapToObject(Optional<UserEntity> optional) {
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        User user = userEntityMapper.fromEntity(optional.get());
        return Optional.of(user);
    }
}
