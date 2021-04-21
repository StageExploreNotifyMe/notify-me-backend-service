package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void getUserById() {
        String id = "test";
        User userById = userService.getUserById(id);
        assertEquals(userById.getId(), id);

    }
}
