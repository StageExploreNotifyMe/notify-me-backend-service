package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("1", new UserPreferences("1", NotificationChannel.EMAIL, NotificationChannel.SMS), "Test", "User", new ArrayList<>());
    }

    private void mockGetById() {
        given(userRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return Optional.of(user);
            }
            return Optional.empty();
        });
    }

    private void mockSave() {
        given(userRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void getById() {
        mockGetById();
        String id = "1";
        Optional<User> optionalUser = userService.getById(id);
        assertTrue(optionalUser.isPresent());
        User userById = optionalUser.get();
        assertEquals(userById.getId(), id);
    }

    @Test
    void getByIdThrowsDbException() {
        given(userRepo.findById(any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> userService.getById("1"));
    }

    @Test
    void addNotificationToInbox() {
        mockSave();
        Notification notification = new Notification();
        notification.setUser(user);

        userService.addNotificationToInbox(notification);
        assertTrue(user.getInbox().contains(notification));
    }

}
