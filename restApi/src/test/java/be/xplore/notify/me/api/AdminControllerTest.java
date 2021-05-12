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
            ResultActions request = mockMvc.perform(get("/admin/notifications/1").contentType(MediaType.APPLICATION_JSON));
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

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

}