package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserPreferencesService userPreferencesService;
    @MockBean
    private UserRepo userRepo;

    @Autowired
    private User user;
    @Autowired
    private Notification notification;

    private void mockGetById() {
        given(userRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return Optional.of(user);
            }
            return Optional.empty();
        });
    }

    @Test
    void getById() {
        mockGetById();
        String id = user.getId();
        Optional<User> optionalUser = userService.findById(id);
        assertTrue(optionalUser.isPresent());
        User userById = optionalUser.get();
        assertEquals(userById.getId(), id);
    }

    @Test
    void addNotificationToInbox() {
        mockSave();
        mockGetById();
        User returnedUser = userService.addNotificationToInbox(notification, user);
        assertTrue(returnedUser.getInbox().stream().anyMatch(n -> n.getId().equals(notification.getId())));
    }

    @Test
    void addNotificationToQueue() {
        mockGetById();
        mockSave();
        User userWithQueue = userService.addNotificationToQueue(notification, user);
        assertTrue(userWithQueue.getNotificationQueue().contains(notification));
    }

    @Test
    void clearUserQueue() {
        mockGetById();
        mockSave();
        User userWithQueue = userService.addNotificationToQueue(notification, user);
        assertTrue(userWithQueue.getNotificationQueue().contains(notification));
        User userWithoutQueue = userService.clearUserQueue(userWithQueue);
        assertTrue(userWithoutQueue.getNotificationQueue().isEmpty());
    }

    @Test
    void getUsersPage() {
        given(userRepo.findAll(any(PageRequest.class))).will(i -> new PageImpl<>(Collections.singletonList(user)));

        Page<User> usersPage = userService.getUsersPage(PageRequest.of(0, 100));
        Assertions.assertTrue(usersPage.hasContent());
        Assertions.assertEquals(1, usersPage.getTotalElements());
        assertEquals(user.getId(), usersPage.getContent().get(0).getId());
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

    @Test
    void registerNewUser() {
        mockSave();
        mockGenerateDefaultPreferences();
        User toRegister = User.builder().firstname("test").lastname("test").mobileNumber("000000000").email("test@email.com").passwordHash("test").build();
        User registered = userService.registerNewUser(toRegister);
        assertEquals(toRegister.getFirstname(), registered.getFirstname());
        assertNotNull(registered.getInbox());
        assertNotNull(registered.getUserPreferences());
        assertNotEquals(toRegister.getPasswordHash(), registered.getPasswordHash());
    }

    @Test
    void registerNewUserAlreadyExists() {
        mockSave();
        mockGenerateDefaultPreferences();
        given(userService.getUserByEmail(any())).willReturn(Optional.of(user));
        User toRegister = User.builder().firstname("test").lastname("test").mobileNumber("000000000").email("test@email.com").passwordHash("test").build();
        assertThrows(AlreadyExistsException.class, () -> userService.registerNewUser(toRegister));
    }

    private void mockGenerateDefaultPreferences() {
        given(userPreferencesService.generateDefaultPreferences()).willReturn(user.getUserPreferences());
    }

    private void mockSave() {
        given(userRepo.save(any())).will(i -> i.getArgument(0));
    }
}
