package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.repositories.UserRepo;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.user.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private UserEntityMapper userEntityMapper;

    @MockBean
    private UserRepo userRepo;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private User user;

    @Autowired
    private UserEntityMapper userEntityMapper;

    @MockBean
    private UserRepo userRepo;

    @Test
    void getNormalChannelUser() {
        mockSave();
        mockUserGetById();
        try {
            ResultActions request = mockMvc.perform(get("/user/1/channel").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
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

    private void mockEverything() {
        given(userService.getById(any())).will(i -> i.getArgument(0).equals(user.getId()) ? Optional.of(user) : Optional.empty());
        given(eventLineService.getAllLinesOfUser(any(), anyInt())).will(i -> new PageImpl<>(Collections.singletonList(eventLine)));
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
    void getNormalChannelUserNotFound() {
        mockSave();
        mockUserGetById();
        try {
            ResultActions request = mockMvc.perform(get("/user/sdf/channel").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void processChangeChannel() {
        try {
            mockSave();
            mockUserGetById();
            UserPreferences userPreferences = user.getUserPreferences();
            String requestBody = mapper.writeValueAsString(new UserPreferencesDto(userPreferences.getId(), userPreferences.getNormalChannel(), userPreferences.getUrgentChannel()));
            ResultActions request = mockMvc.perform(post("/user/1/preferences/channel").content(requestBody).contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserPreferencesNotificationChannel() {
        try {
            ResultActions request = mockMvc.perform(get("/user/preferences").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mockUserGetById() {
        given(userRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return Optional.of(userEntityMapper.toEntity(user));
            }
            return Optional.empty();
        });
    }

    private void mockSave() {
        given(userRepo.save(any())).will(i -> i.getArgument(0));
    }
}