package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.user.UserOrganizationDto;
import be.xplore.notify.me.dto.user.UserOrganizationIdsDto;
import be.xplore.notify.me.dto.user.UserOrganizationProcessDto;
import be.xplore.notify.me.persistence.OrganizationRepo;
import be.xplore.notify.me.persistence.UserOrganizationRepo;
import be.xplore.notify.me.persistence.UserRepo;
import be.xplore.notify.me.services.user.UserOrganizationNotificationService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserOrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrganizationRepo organizationRepo;
    @MockBean
    private UserOrganizationRepo userOrganizationRepo;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private UserOrganizationNotificationService userOrganizationNotificationService;

    @Autowired
    private User user;
    @Autowired
    private Organization organization;
    @Autowired
    private UserOrganization request;

    @Test
    void userJoinOrganization() {
        try {
            mockAll();
            ResultActions resultActions = getPerformPostWithContent("/userorganization/request/join", new UserOrganizationIdsDto(user.getId(), organization.getId()));
            expectStatus(HttpStatus.CREATED, resultActions);
            UserOrganizationDto userOrganization = mapper.readValue(getContentAsString(resultActions), UserOrganizationDto.class);

            assertEquals(user.getId(), userOrganization.getUser().getId());
            assertEquals(organization.getId(), userOrganization.getOrganization().getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void userJoinNotFound() {
        try {
            mockFetchesByIds();
            ResultActions resultActions = getPerformPostWithContent("/userorganization/request/join", new UserOrganizationIdsDto("qqsdfqsdf", organization.getId()));
            expectStatus(HttpStatus.NOT_FOUND, resultActions);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void processJoinRequest() {
        try {
            mockAll();
            ResultActions resultActions = getPerformPostWithContent("/userorganization/request/process", new UserOrganizationProcessDto(request.getId(), true));
            expectStatus(HttpStatus.OK, resultActions);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void processJoinRequestNotFound() {
        try {
            mockAll();
            ResultActions request = getPerformPostWithContent("/userorganization/request/process", new UserOrganizationProcessDto("qksdfj", true));
            expectStatus(HttpStatus.NOT_FOUND, request);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getPendingJoinRequests() {
        try {
            mockAll();
            ResultActions request = getPerform(get("/userorganization/requests/1/pending?page=1"));
            expectStatus(HttpStatus.OK, request);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getUsersOfOrganization() {
        try {
            mockAll();
            ResultActions resultActions = getPerform(get("/userorganization/" + request.getId() + "/users?page=1"));
            expectStatus(HttpStatus.OK, resultActions);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getUsersOfOrganizationInvalidId() {
        try {
            mockAll();
            ResultActions request = getPerform(get("/userorganization/maermkdoiu/users"));
            expectStatus(HttpStatus.NOT_FOUND, request);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getUsersOfOrganizationNoPage() {
        try {
            mockAll();
            ResultActions resultActions = getPerform(get("/userorganization/" + request.getId() + "/users"));
            expectStatus(HttpStatus.OK, resultActions);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void promoteMember() {
        try {
            mockAll();
            ResultActions resultActions = getPerform(post("/userorganization/" + request.getId() + "/promote"));
            expectStatus(HttpStatus.OK, resultActions);
            UserOrganizationDto userOrganizationDto = mapper.readValue(getContentAsString(resultActions), UserOrganizationDto.class);
            assertEquals(Role.ORGANIZATION_LEADER, userOrganizationDto.getRole());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void promoteMemberNotFound() {
        try {
            mockAll();
            ResultActions resultActions = getPerform(post("/userorganization/qdsmlfk/promote"));
            expectStatus(HttpStatus.NOT_FOUND, resultActions);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void promoteMemberWrongMethod() {
        try {
            mockAll();
            ResultActions resultActions = getPerform(patch("/userorganization/qdsmlfk/promote"));
            expectStatus(HttpStatus.METHOD_NOT_ALLOWED, resultActions);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void demoteMember() {
        try {
            mockAll();
            MockHttpServletRequestBuilder patch = post("/userorganization/" + request.getId() + "/demote");
            ResultActions resultActions = getPerform(patch);
            expectStatus(HttpStatus.OK, resultActions);
            UserOrganizationDto userOrganizationDto = mapper.readValue(getContentAsString(resultActions), UserOrganizationDto.class);
            assertEquals(Role.MEMBER, userOrganizationDto.getRole());
        } catch (Exception e) {
            failTest(e);
        }
    }

    private ResultActions getPerform(MockHttpServletRequestBuilder patch) throws Exception {
        return mockMvc.perform(patch.contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions getPerformPostWithContent(String url, Object requestBody) throws Exception {
        return getPerform(post(url).content(mapper.writeValueAsString(requestBody)));
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    private String getContentAsString(ResultActions request) throws UnsupportedEncodingException {
        return request.andReturn().getResponse().getContentAsString();
    }

    private void expectStatus(HttpStatus status, ResultActions request) throws Exception {
        request.andExpect(status().is(status.value()));
    }

    private void mockAll() {
        mockSaves();
        mockFetchesByIds();
    }

    private void mockFetchesByIds() {
        mockUserFetchById();
        mockOrganizationFetchById();
        mockUserOrganizationFetchById();
        mockUserOrganisationByOrganization_IdAndStatus();
    }

    private void mockSaves() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserByOrganizationAndStatus(any(), any(), any())).will(i -> {
            List<UserOrganization> pending = new ArrayList<>();
            pending.add(request);
            return new PageImpl<>(pending);
        });
    }

    private void mockUserOrganizationFetchById() {
        given(userOrganizationRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(request.getId())) {
                return Optional.of(request);
            }
            return Optional.empty();
        });
    }

    private void mockUserFetchById() {
        given(userRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return Optional.of(user);
            }
            return Optional.empty();
        });
    }

    private void mockOrganizationFetchById() {
        given(organizationRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(organization.getId())) {
                return Optional.of(organization);
            }
            return Optional.empty();
        });
    }
}