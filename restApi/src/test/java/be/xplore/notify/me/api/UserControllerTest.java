package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.dto.user.UserPreferencesDto;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private ObjectMapper mapper;

    @Test
    void getNormalChannelUser() {
        try {
            mockEverything();
            ResultActions request = mockMvc.perform(get("/user/1/channel").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getNormalChannelUserNotFound() {
        try {
            mockEverything();
            ResultActions request = mockMvc.perform(get("/user/sdf/channel").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserLines() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/user/" + user.getId() + "/lines");
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getUserLinesUserNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/user/qmldfkj/lines?page=0");
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void processChangeChannel() {
        try {
            mockEverything();
            UserPreferences userPreferences = user.getUserPreferences();
            Object dto = new UserPreferencesDto(userPreferences.getId(), userPreferences.getNormalChannel(), userPreferences.getUrgentChannel());
            ResultActions request = performPost("/user/1/preferences/channel", dto);
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getUserPreferencesNotificationChannel() {
        try {
            ResultActions request = performGet("/user/preferences");
            expectResult(request, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getAllUsers() {
        try {
            mockEverything();
            ResultActions request = performGet("/user");
            expectResult(request, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    private ResultActions performPost(String url, Object dto) throws Exception {
        return mockMvc.perform(post(url).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
    }

    private void mockEverything() {
        given(userService.getById(any())).will(i -> i.getArgument(0).equals(user.getId()) ? Optional.of(user) : Optional.empty());
        given(eventLineService.getAllLinesOfUser(any(), anyInt())).will(i -> new PageImpl<>(Collections.singletonList(eventLine)));
        given(userService.getUsersPage(any())).will(i -> new PageImpl<>(Collections.singletonList(user)));
    }

    private ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON));
    }

    private void expectResult(ResultActions resultActions, HttpStatus status) throws Exception {
        resultActions.andExpect(status().is(status.value()));
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }
}