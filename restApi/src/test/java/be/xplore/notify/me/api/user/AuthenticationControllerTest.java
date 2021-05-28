package be.xplore.notify.me.api.user;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.user.LoggedInDto;
import be.xplore.notify.me.dto.user.LoginDto;
import be.xplore.notify.me.dto.user.UserDto;
import be.xplore.notify.me.dto.user.UserRegisterDto;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private User user;

    @Test
    void login() {
        try {
            mockGetUser();
            ResultActions resultActions = TestUtils.performPost(mockMvc, new LoginDto(user.getEmail(), "John"), "/login");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            LoggedInDto loggedInDto = mapper.readValue(TestUtils.getContentAsString(resultActions), LoggedInDto.class);
            assertEquals(user.getId(), loggedInDto.getUserDto().getId());
            assertNotNull(loggedInDto.getJwt());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void loginWrongPassword() {
        try {
            mockGetUser();
            ResultActions resultActions = TestUtils.performPost(mockMvc, new LoginDto(user.getEmail(), "NotJohn"), "/login");
            TestUtils.expectStatus(resultActions, HttpStatus.valueOf(401));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void loginUnknownUser() {
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, new LoginDto(user.getEmail(), "NotJohn"), "/login");
            TestUtils.expectStatus(resultActions, HttpStatus.valueOf(401));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void register() {
        try {
            mockRegisterNewUser();
            UserRegisterDto userRegisterDto = generateRegisterUserDto(user.getEmail());
            ResultActions resultActions = TestUtils.performPost(mockMvc, userRegisterDto, "/authentication/register");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            UserDto userDto = mapper.readValue(TestUtils.getContentAsString(resultActions), UserDto.class);
            assertEquals(userRegisterDto.getFirstname(), userDto.getFirstname());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void registerBadRequestEmailEmpty() {
        try {
            mockRegisterNewUser();
            UserRegisterDto userRegisterDto = generateRegisterUserDto("");
            ResultActions resultActions = TestUtils.performPost(mockMvc, userRegisterDto, "/authentication/register");
            TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void registerBadRequestEmailWithoutDot() {
        try {
            mockRegisterNewUser();
            UserRegisterDto userRegisterDto = generateRegisterUserDto("qsdf@qdf");
            ResultActions resultActions = TestUtils.performPost(mockMvc, userRegisterDto, "/authentication/register");
            TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void registerBadRequestEmailWithoutAt() {
        try {
            mockRegisterNewUser();
            UserRegisterDto userRegisterDto = generateRegisterUserDto("qsdf.qdf");
            ResultActions resultActions = TestUtils.performPost(mockMvc, userRegisterDto, "/authentication/register");
            TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private UserRegisterDto generateRegisterUserDto(String s) {
        return new UserRegisterDto(user.getFirstname(), user.getLastname(), s, user.getMobileNumber(), "test");
    }

    private void mockRegisterNewUser() {
        given(userService.registerNewUser(any())).willReturn(user);
    }

    private void mockGetUserByMail() {
        given(userService.getUserByEmail(any())).willReturn(Optional.of(user));
    }

    private void mockGetUserById() {
        given(userService.findById(any())).willReturn(Optional.of(user));
        given(userService.getById(any())).willReturn(user);
    }

    private void mockGetUser() {
        mockGetUserByMail();
        mockGetUserById();
    }
}