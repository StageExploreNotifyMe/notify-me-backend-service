package be.xplore.notify.me.api;

import be.xplore.notify.me.api.dto.OrganizationDto;
import be.xplore.notify.me.api.dto.UserDto;
import be.xplore.notify.me.api.dto.UserOrganizationDto;
import be.xplore.notify.me.api.dto.UserOrganizationProcessDto;
import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.domain.user.UserPreferences;
import be.xplore.notify.me.repositories.OrganizationRepo;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import be.xplore.notify.me.repositories.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserOrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    private User user;
    private Organization organization;
    private UserOrganization request;
    private UserDto userDto;
    private OrganizationDto organizationDto;

    @MockBean
    private UserOrganizationRepo userOrganizationRepo;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private OrganizationRepo organizationRepo;

    private void mockSaves() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockFetchByIds() {
        given(userOrganizationRepo.findById(any())).will(i -> i.getArgument(0).equals(request.getId()) ? Optional.of(request) : Optional.empty());
        given(userRepo.findById(any())).will(i -> i.getArgument(0).equals(user.getId()) ? Optional.of(user) : Optional.empty());
        given(organizationRepo.findById(any())).will(i -> i.getArgument(0).equals(organization.getId()) ? Optional.of(organization) : Optional.empty());
    }

    @BeforeEach
    void setUp() {
        user = new User("1", new UserPreferences("1", NotificationChannel.EMAIL, NotificationChannel.SMS), "Test", "User", new ArrayList<>());
        organization = new Organization("1", "Example Organization");
        request = new UserOrganization("1", user, organization, Role.MEMBER, MemberRequestStatus.PENDING);
        userDto = modelMapper.map(user, UserDto.class);
        organizationDto = modelMapper.map(organization, OrganizationDto.class);
    }

    @Test
    void userJoinOrganization() {
        try {
            mockSaves();
            mockFetchByIds();
            String result = performJoinRequestWithBody(mapper.writeValueAsString(new UserOrganizationDto(null, null, null, userDto, organizationDto)), HttpStatus.CREATED.value());
            UserOrganization userOrganization = mapper.readValue(result, UserOrganization.class);

            Assertions.assertEquals(user.getId(), userOrganization.getUser().getId());
            Assertions.assertEquals(organization.getId(), userOrganization.getOrganization().getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void userJoinNotFound() {
        try {
            mockFetchByIds();
            given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
            userDto.setId("fsdqfd");
            performJoinRequestWithBody(mapper.writeValueAsString(new UserOrganizationDto(null, null, null, userDto, organizationDto)), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void userJoinNotFound2() {
        try {
            mockFetchByIds();
            given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
            organizationDto.setId("dfsdf");
            performJoinRequestWithBody(mapper.writeValueAsString(new UserOrganizationDto(null, null, null, userDto, organizationDto)), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void userJoinException() {
        try {
            mockFetchByIds();
            given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
            performJoinRequestWithBody(mapper.writeValueAsString(new UserOrganizationDto("", null, null, userDto, organizationDto)), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void processJoinRequest() {
        try {
            mockFetchByIds();
            mockSaves();
            String requestBody = mapper.writeValueAsString(new UserOrganizationProcessDto(request.getId(), true));
            ResultActions request = mockMvc.perform(post("/userorganization/request/process").content(requestBody).contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void processJoinRequestNotFound() {
        try {
            mockFetchByIds();
            mockSaves();
            String requestBody = mapper.writeValueAsString(new UserOrganizationProcessDto(request.getId() + "qksdfj", true));
            ResultActions request = mockMvc.perform(post("/userorganization/request/process").content(requestBody).contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getPendingJoinRequests() {
        try {
            mockSaves();
            mockFetchByIds();
            mockUserOrganisationByOrganization_IdAndStatus();
            ResultActions request = mockMvc.perform(get("/userorganization/requests/1/pending/1").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    private String performJoinRequestWithBody(String requestBody, int status) throws Exception {
        ResultActions request = mockMvc.perform(post("/userorganization/request/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        ResultActions expectedRequest = request.andExpect(status().is(status));
        return expectedRequest.andReturn().getResponse().getContentAsString();
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserOrganisationByOrganization_IdAndStatus(any(), any(), any())).will(i -> {
            List<UserOrganization> pending = new ArrayList<>();
            pending.add(request);
            return new PageImpl<>(pending);
        });
    }
}