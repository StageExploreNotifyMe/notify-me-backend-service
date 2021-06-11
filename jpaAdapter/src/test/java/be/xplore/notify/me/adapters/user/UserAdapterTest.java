package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class UserAdapterTest {

    @Autowired
    private UserAdapter userAdapter;

    @Test
    void findById() {
        Optional<User> byId = userAdapter.findById("1");
        assertTrue(byId.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<User> byId = userAdapter.findById("500");
        assertTrue(byId.isEmpty());
    }

    @Test
    void save() {
        User user = User.builder().id("501").firstname("Test").build();
        User save = userAdapter.save(user);
        assertEquals(user.getFirstname(), save.getFirstname());
    }

    @Test
    void findAll() {
        Page<User> all = userAdapter.findAll(PageRequest.of(0, 20));
        assertTrue(all.hasContent());
    }

    @Test
    void getUserByEmail() {
        Optional<User> userByEmail = userAdapter.getUserByEmail("test@email.com");
        assertTrue(userByEmail.isPresent());
    }
}