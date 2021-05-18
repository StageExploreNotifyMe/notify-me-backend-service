package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo {
    Optional<User> findById(String id);

    User save(User user);

    Page<User> findAll(PageRequest pageRequest);
}
