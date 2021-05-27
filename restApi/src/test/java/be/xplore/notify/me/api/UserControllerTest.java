package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.dto.user.UserPreferencesDto;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.user.UserService;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventLineService eventLineService;
    @MockBean
    private UserService userService;

    @Autowired
    private User user;
    @Autowired
    private EventLine eventLine;

    @Test
    void getNormalChannelUser() {
        try {
            mockEverything();
            ResultActions request = TestUtils.performGet(mockMvc, "/user/1/channel");

            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getNormalChannelUserNotFound() {
        try {
            mockEverything();
            ResultActions request = TestUtils.performGet(mockMvc, "/user/sdf/channel");
            request.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserLines() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/user/" + user.getId() + "/lines");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getUserLinesUserNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/user/qmldfkj/lines?page=0");
            TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void processChangeChannel() {
        try {
            mockEverything();
            UserPreferences userPreferences = user.getUserPreferences();
            Object dto = new UserPreferencesDto(userPreferences.getId(), userPreferences.getNormalChannel(), userPreferences.getUrgentChannel());
            ResultActions request = TestUtils.performPost(mockMvc, dto, "/user/1/preferences/channel");
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getUserPreferencesNotificationChannel() {
        try {
            ResultActions request = TestUtils.performGet(mockMvc, "/user/preferences");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllUsers() {
        try {
            mockEverything();
            ResultActions request = TestUtils.performGet(mockMvc, "/user");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockEverything() {
        given(userService.getById(any())).will(i -> i.getArgument(0).equals(user.getId()) ? Optional.of(user) : Optional.empty());
        given(eventLineService.getAllLinesOfUser(any(), anyInt())).will(i -> new PageImpl<>(Collections.singletonList(eventLine)));
        given(userService.getUsersPage(any())).will(i -> new PageImpl<>(Collections.singletonList(user)));
    }
}