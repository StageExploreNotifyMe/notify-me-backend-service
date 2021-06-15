package be.xplore.notify.me.api.admin;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.notification.NotificationService;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    Notification notification;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;
    @MockBean
    private EventService eventService;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        given(eventService.getAllById(any())).willReturn(new ArrayList<>());
        given(userService.getAllById(any())).willReturn(new ArrayList<>());
    }

    @Test
    void getAllNotifications() {
        try {
            mockSave();
            mockGetAllNotifications();
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/notifications?page=0");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllNotificationsUnAuthorized() {
        try {
            ResultActions perform = mockMvc.perform(get("/admin/notifications?page=0").contentType(MediaType.APPLICATION_JSON));
            TestUtils.expectStatus(perform, HttpStatus.valueOf(403));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllNotificationsByType() {
        try {
            mockSave();
            mockGetAllNotificationByType();
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/notifications/type/USER_JOINED?page=0");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllNotificationsByEvent() {
        try {
            mockSave();
            mockGetAllNotificationsByEvent();
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/notifications/event/1?page=0");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllNotificationsByTypeAndEvent() {
        try {
            mockSave();
            mockGetAllNotificationByTypeAndEvent();
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/notifications/type/USER_JOINED/event/1?page=0");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllNotificationTypes() {
        try {
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/notificationTypes");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllEvents() {
        try {
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/eventId");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAmountOfNotificationChannels() {
        try {
            ResultActions request = TestUtils.performGet(mockMvc, "/admin/channelAmount");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockSave() {
        given(notificationService.save(any())).will(i -> i.getArgument(0));
    }

    private void mockGetAllNotifications() {
        given(notificationService.getAllNotifications(any())).will(i -> {
            List<Notification> notifications = new ArrayList<>();
            notifications.add(notification);
            return new PageImpl<>(notifications);
        });
    }

    private void mockGetAllNotificationsByEvent() {
        given(notificationService.getAllNotificationsByEventId(any(), any())).will(i -> {
            List<Notification> notifications = new ArrayList<>();
            notifications.add(notification);
            return new PageImpl<>(notifications);
        });
    }

    private void mockGetAllNotificationByType() {
        given(notificationService.getAllNotificationsByType(any(), any())).will(i -> {
            List<Notification> notifications = new ArrayList<>();
            notifications.add(notification);
            return new PageImpl<>(notifications);
        });
    }

    private void mockGetAllNotificationByTypeAndEvent() {
        given(notificationService.getAllByTypeAndEvent(any(), any(), any())).will(i -> {
            List<Notification> notifications = new ArrayList<>();
            notifications.add(notification);
            return new PageImpl<>(notifications);
        });
    }

}