package be.xplore.notify.me.util.mockadapters.user;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAdapter implements UserRepo {

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Page<User> findAll(PageRequest pageRequest) {
        return null;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }
}
