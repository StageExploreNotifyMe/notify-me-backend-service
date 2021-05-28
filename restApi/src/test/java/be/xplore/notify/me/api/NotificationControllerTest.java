package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.persistence.NotificationRepo;
import be.xplore.notify.me.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationRepo notificationRepo;

    @Autowired
    private Notification notification;

    @Test
    void getUserNotifications() {
        try {
            mockSave();
            mockGetAllByUserId();
            ResultActions request = TestUtils.performGet(mockMvc, "/user/inbox/1/pending/1");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockSave() {
        given(notificationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockGetAllByUserId() {
        given(notificationRepo.getAllByUserId(any(), any())).will(i -> {
            List<Notification> notifications = new ArrayList<>();
            notifications.add(notification);
            return new PageImpl<>(notifications);
        });
    }

}