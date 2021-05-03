package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.services.RepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService extends RepoService<User, UserEntity> {

    public UserService(JpaRepository<UserEntity, String> repo, EntityMapper<UserEntity, User> entityMapper) {
        super(repo, entityMapper);
    }

    public User addNotificationToInbox(Notification notification) {
        Optional<User> userOptional = getById(notification.getUserId());
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found for id " + notification.getUserId());
        }
        User user = userOptional.get();
        user.getInbox().add(notification);
        return save(user);
    }

}
