package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.UserPreferences;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class UserPreferencesAdapterTest {

    @Autowired
    private UserPreferencesAdapter userPreferencesAdapter;

    @Test
    void save() {
        UserPreferences userPreferences = UserPreferences.builder().urgentChannel(NotificationChannel.SMS).build();
        UserPreferences save = userPreferencesAdapter.save(userPreferences);
        assertEquals(userPreferences.getUrgentChannel(), save.getUrgentChannel());
    }
}