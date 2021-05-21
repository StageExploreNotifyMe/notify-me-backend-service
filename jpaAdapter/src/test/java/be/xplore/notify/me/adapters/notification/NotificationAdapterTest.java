package be.xplore.notify.me.adapters.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class NotificationAdapterTest {

    @Autowired
    private NotificationAdapter notificationAdapter;

    @Test
    void findById() {
        Optional<Notification> byId = notificationAdapter.findById("1");
        assertTrue(byId.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<Notification> byId = notificationAdapter.findById("qsdmfklj");
        assertTrue(byId.isEmpty());
    }

    @Test
    void save() {
        Notification notification = Notification.builder().body("test").build();
        Notification save = notificationAdapter.save(notification);
        assertEquals(notification.getBody(), save.getBody());
    }

    @Test
    void getAllByUserId() {
        Page<Notification> page = notificationAdapter.getAllByUserId("1", PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void getAllByNotificationType() {
        Page<Notification> page = notificationAdapter.getAllByNotificationType(NotificationType.EVENT_CREATED, PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void getAll() {
        Page<Notification> page = notificationAdapter.getAll(PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void getAllByEventId() {
        Page<Notification> page = notificationAdapter.getAllByEventId("1", PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void getAllByTypeAndEvent() {
        Page<Notification> page = notificationAdapter.getAllByTypeAndEvent(NotificationType.EVENT_CREATED, "1", PageRequest.of(0, 20));
        assertTrue(page.hasContent());
    }

    @Test
    void gelAllEventIds() {
        List<String> list = notificationAdapter.getAllEventIds();
        assertFalse(list.isEmpty());
    }

    @Test
    void getChannelAmount() {
        List<Object[]> list = notificationAdapter.getChannelAmount();
        assertFalse(list.isEmpty());
    }
}