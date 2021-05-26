package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.persistence.UserPreferencesRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserPreferencesServiceTest {

    @Autowired
    private UserPreferencesService userPreferencesService;

    @Autowired
    private User user;

    @MockBean
    private UserPreferencesRepo userPreferencesRepo;

    @BeforeEach
    void setUp() {
        mockSave();
    }

    @Test
    void setNotificationChannels() {
        NotificationChannel toSetTo = NotificationChannel.APP;
        UserPreferences preferences = userPreferencesService.setNotificationChannels(user, toSetTo, toSetTo);
        assertEquals(toSetTo, preferences.getNormalChannel());
        assertEquals(toSetTo, preferences.getUrgentChannel());
    }

    @Test
    void save() {
        UserPreferences saved = userPreferencesRepo.save(user.getUserPreferences());
        assertEquals(user.getUserPreferences().getId(), saved.getId());
    }

    @Test
    void generateDefaultPreferences() {
        UserPreferences userPreferences = userPreferencesService.generateDefaultPreferences();
        assertEquals(NotificationChannel.EMAIL, userPreferences.getNormalChannel());
        assertEquals(NotificationChannel.SMS, userPreferences.getUrgentChannel());
    }

    private void mockSave() {
        given(userPreferencesRepo.save(any())).will(i -> i.getArgument(0));
    }
}