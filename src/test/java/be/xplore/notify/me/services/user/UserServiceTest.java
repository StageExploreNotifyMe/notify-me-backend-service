package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.repositories.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Autowired
    private User user;
    @Autowired
    private Notification notification;

    private void mockGetById() {
        given(userRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return Optional.of(userEntityMapper.toEntity(user));
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
        String id = user.getId();
        Optional<User> optionalUser = userService.getById(id);
        assertTrue(optionalUser.isPresent());
        User userById = optionalUser.get();
        assertEquals(userById.getId(), id);
    }

    @Test
    void addNotificationToInbox() {
        mockSave();
        mockGetById();
        User returnedUser = userService.addNotificationToInbox(notification);
        assertTrue(returnedUser.getInbox().stream().anyMatch(n -> n.getId().equals(notification.getId())));
    }

    @Test
    void addNotificationToInboxUserNotFound() {
        mockSave();
        mockGetById();
        assertThrows(NotFoundException.class, () -> userService.addNotificationToInbox(Notification.builder().userId("qdsfae").build()));
    }

    @Test
    void setNotificationChannelsUserNotFound() {
        mockSave();
        mockGetById();
        NotificationChannel normalChannel = NotificationChannel.EMAIL;
        NotificationChannel urgentChannel = NotificationChannel.SMS;
        assertThrows(NotFoundException.class, () -> userService.setNotificationChannels("dsfqfdq", normalChannel, urgentChannel));
    }

    @Test
    void setNotificationChannels() {
        mockSave();
        mockGetById();
        NotificationChannel normalChannel = NotificationChannel.EMAIL;
        NotificationChannel urgentChannel = NotificationChannel.SMS;
        User returnedUser = userService.setNotificationChannels("1", normalChannel, urgentChannel);
        assertEquals(returnedUser.getUserPreferences().getNormalChannel(), user.getUserPreferences().getNormalChannel());
    }
}
