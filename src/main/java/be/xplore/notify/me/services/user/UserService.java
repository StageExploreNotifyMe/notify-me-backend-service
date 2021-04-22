package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> getById(String id) {
        try {
            return userRepo.findById(id);
        } catch (Exception e) {
            log.error("Failed to fetch user with id {}: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public User addNotificationToInbox(Notification notification) {
        User user = notification.getUser();
        user.getInbox().add(notification);
        return saveUser(user);
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }
}
