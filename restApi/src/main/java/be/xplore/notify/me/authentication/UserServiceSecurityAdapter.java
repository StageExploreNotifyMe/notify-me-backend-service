package be.xplore.notify.me.authentication;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Collections.emptyList;

@Component
public class UserServiceSecurityAdapter implements UserDetailsService {
    private final UserService userService;

    public UserServiceSecurityAdapter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        Optional<User> optionalUser = userService.getUserByEmail(userIdentifier);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(userIdentifier);
        }
        User user = optionalUser.get();
        return new org.springframework.security.core.userdetails.User(user.getId(), user.getPasswordHash(), emptyList());
    }
}
