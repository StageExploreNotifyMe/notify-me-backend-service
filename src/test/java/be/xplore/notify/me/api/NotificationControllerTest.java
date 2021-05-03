package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.entity.mappers.notification.NotificationEntityMapper;
import be.xplore.notify.me.entity.notification.NotificationEntity;
import be.xplore.notify.me.repositories.NotificationRepo;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationEntityMapper notificationEntityMapper;

    @MockBean
    private NotificationRepo notificationRepo;

    @Autowired
    private Notification notification;

    @Test
    void getUserNotifications() {
        try {
            mockSave();
            mockGetAllByUserId();
            ResultActions request = mockMvc.perform(get("/user/inbox/1/pending/1").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void mockSave() {
        given(notificationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockGetAllByUserId() {
        given(notificationRepo.getAllByUserId(any(), any())).will(i -> {
            List<NotificationEntity> notificationEntities = new ArrayList<>();
            notificationEntities.add(notificationEntityMapper.toEntity(notification));
            return new PageImpl<>(notificationEntities);
        });
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

}