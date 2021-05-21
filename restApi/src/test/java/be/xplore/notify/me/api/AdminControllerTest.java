package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.services.notification.NotificationService;
import org.junit.jupiter.api.Assertions;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    Notification notification;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void getAllNotifications() {
        try {
            mockSave();
            mockGetAllNotifications();
            ResultActions request = mockMvc.perform(get("/admin/notifications?page=0").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAllNotificationsByType() {
        try {
            mockSave();
            mockGetAllNotificationByType();
            ResultActions request = mockMvc.perform(get("/admin/notifications/type/USER_JOINED?page=0").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAllNotificationsByEvent() {
        try {
            mockSave();
            mockGetAllNotificationsByEvent();
            ResultActions request = mockMvc.perform(get("/admin/notifications/event/1?page=0").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAllNotificationsByTypeAndEvent() {
        try {
            mockSave();
            mockGetAllNotificationByTypeAndEvent();
            ResultActions request = mockMvc.perform(get("/admin/notifications/type/USER_JOINED/event/1?page=0").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAllNotificationTypes() {
        try {
            ResultActions request = mockMvc.perform(get("/admin/notificationTypes").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAllEvents() {
        try {
            ResultActions request = mockMvc.perform(get("/admin/eventId").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAmountOfNotificationChannels() {
        try {
            ResultActions request = mockMvc.perform(get("/admin/channelAmount").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
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

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

}