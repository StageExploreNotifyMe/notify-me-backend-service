package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User getUserById(String userId) {
        return new User(userId);
    }
}
