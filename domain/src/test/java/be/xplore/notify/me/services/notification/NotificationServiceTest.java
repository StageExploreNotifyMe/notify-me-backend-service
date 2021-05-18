package be.xplore.notify.me.services.notification;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.notification.NotificationType;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.NotificationRepo;
import be.xplore.notify.me.services.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private NotificationRepo notificationRepo;
    @MockBean
    private UserService userService;

    @Autowired
    private Notification notification;
    @Autowired
    private User user;

    private void mockGetById() {
        given(notificationRepo.findById(any()))
                .will(i -> i.getArgument(0).equals(notification.getId()) ? Optional.of(notification) : Optional.empty());
    }

    private void mockGetAll() {
        given(notificationRepo.getAll(any())).will(i -> getPageOfNotification());
    }

    private void mockGetAllByUserId() {
        given(notificationRepo.getAllByUserId(any(), any())).will(i -> getPageOfNotification());
    }

    private void mockGetAllByNotificationType() {
        given(notificationRepo.getAllByNotificationType(any(), any())).will(i -> getPageOfNotification());
    }

    private void mockGetAllByTypeAndEvent() {
        given(notificationRepo.getAllByTypeAndEvent(any(), any(), any())).will(i -> getPageOfNotification());
    }

    private void MockGetAllByEventId() {
        given(notificationRepo.getAllByEventId(any(), any())).will(i -> getPageOfNotification());
    }

    private void mockGetAllEventIds() {
        given(notificationRepo.getAllEventIds()).will(i -> getEventsOfNotifications());
    }

    private void mockGetChannelAmount() {
        given(notificationRepo.getChannelAmount()).will(i -> getAllChannelAmount());
    }

    private List<Object[]> getAllChannelAmount() {
        List<Object[]> list = new ArrayList<>();
        list.add(0, NotificationChannel.values());
        return list;
    }

    private List<String> getEventsOfNotifications() {
        List<String> events = new ArrayList<>();
        events.add(notification.getEventId());
        return events;
    }

    private Object getPageOfNotification() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        return new PageImpl<>(notifications);
    }

    private void mockAddNotificationToInbox() {
        given(userService.addNotificationToInbox(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            user.getInbox().add(n);
            return user;
        });
    }

    private void mockAddNotificationToQueue() {
        given(userService.addNotificationToInbox(any(), any())).will(i -> {
            Notification n = i.getArgument(0);
            user.getNotificationQueue().add(n);
            return user;
        });
    }

    private void mockSaveNotification() {
        given(notificationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void saveNotificationAndSendToInbox() {
        mockAddNotificationToInbox();
        mockSaveNotification();
        Notification returnedNotification = notificationService.saveNotificationAndSendToInbox(notification, user);
        assertEquals(notification.getId(), returnedNotification.getId());
        assertTrue(user.getInbox().stream().anyMatch(n -> n.getId().equals(notification.getId())));
    }

    @Test
    void saveNotificationAndSendToQueue() {
        mockAddNotificationToQueue();
        mockSaveNotification();
        Notification returnedNotification = notificationService.saveNotificationAndSendToQueue(notification);
        assertEquals(notification.getId(), returnedNotification.getId());
        assertTrue(user.getInbox().stream().anyMatch(n -> n.getId().equals(notification.getId())));
    }

    @Test
    void getById() {
        mockGetById();
        Optional<Notification> notificationOptional = notificationService.getById(notification.getId());
        assertTrue(notificationOptional.isPresent());
        assertEquals(notification.getId(), notificationOptional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        mockGetById();
        Optional<Notification> notificationOptional = notificationService.getById("qdsf");
        assertTrue(notificationOptional.isEmpty());
    }

    @Test
    void getAllNotifications() {
        mockGetAll();
        Page<Notification> notifications = notificationService.getAllNotifications(PageRequest.of(0, 20));
        Assertions.assertEquals(1, notifications.getContent().size());
    }

    @Test
    void getAllNotificationsByUserId() {
        mockGetAllByUserId();
        Page<Notification> notifications = notificationService.getAllNotificationsByUserId(user.getId(), PageRequest.of(0, 20));
        Assertions.assertEquals(1, notifications.getContent().size());
    }

    @Test
    void getAllNotificationsByType() {
        mockGetAllByNotificationType();
        Page<Notification> notifications = notificationService.getAllNotificationsByType(NotificationType.EVENT_CANCELED, PageRequest.of(0, 20));
        Assertions.assertEquals(1, notifications.getContent().size());
    }

    @Test
    void getAllNotificationsByEvent() {
        MockGetAllByEventId();
        Page<Notification> notifications = notificationService.getAllNotificationsByEventId("1", PageRequest.of(0, 20));
        Assertions.assertEquals(1, notifications.getContent().size());
    }

    @Test
    void getAllNotificationsByTypeAndEvent() {
        mockGetAllByTypeAndEvent();
        Page<Notification> notifications = notificationService.getAllByTypeAndEvent("1", NotificationType.EVENT_CANCELED, PageRequest.of(0, 20));
        Assertions.assertEquals(1, notifications.getContent().size());
    }

    @Test
    void getAllEventIds() {
        mockGetAllEventIds();
        List<String> events = notificationService.getAllEventIds();
        Assertions.assertEquals(1, events.size());
    }

    @Test
    void getChannelAmount() {
        mockGetChannelAmount();
        List<Object[]> amounts = notificationService.getChannelAmount();
        Assertions.assertEquals(1, amounts.size());

    }
}