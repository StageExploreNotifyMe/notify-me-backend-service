package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.user.UserOrganizationDto;
import be.xplore.notify.me.dto.user.UserOrganizationIdsDto;
import be.xplore.notify.me.dto.user.UserOrganizationProcessDto;
import be.xplore.notify.me.dto.user.UserOrganizationsDto;
import be.xplore.notify.me.persistence.OrganizationRepo;
import be.xplore.notify.me.persistence.UserOrganizationRepo;
import be.xplore.notify.me.persistence.UserRepo;
import be.xplore.notify.me.services.user.UserOrganizationNotificationService;
import be.xplore.notify.me.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

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
    private UserOrganization userOrganization;

    @Test
    void userJoinOrganization() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPost(mockMvc, new UserOrganizationIdsDto(user.getId(), organization.getId()), "/userorganization/request/join");
            TestUtils.expectStatus(request, HttpStatus.CREATED);
            UserOrganizationDto userOrganization = mapper.readValue(TestUtils.getContentAsString(request), UserOrganizationDto.class);

            assertEquals(user.getId(), userOrganization.getUser().getId());
            assertEquals(organization.getId(), userOrganization.getOrganization().getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void userJoinNotFound() {
        try {
            mockFetchesByIds();
            ResultActions request = TestUtils.performPost(mockMvc, new UserOrganizationIdsDto("qqsdfqsdf", organization.getId()), "/userorganization/request/join");
            TestUtils.expectStatus(request, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void processJoinRequest() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPost(mockMvc, new UserOrganizationProcessDto(userOrganization.getId(), true), "/userorganization/request/process");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void processJoinRequestNotFound() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPost(mockMvc, new UserOrganizationProcessDto("qksdfj", true), "/userorganization/request/process");
            TestUtils.expectStatus(request, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getPendingJoinRequests() {
        try {
            mockAll();
            ResultActions request = TestUtils.performGet(mockMvc, "/userorganization/requests/1/pending?page=1");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getUsersOfOrganization() {
        try {
            mockAll();
            ResultActions request = TestUtils.performGet(mockMvc, "/userorganization/" + userOrganization.getId() + "/users?page=1");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getUsersOfOrganizationInvalidId() {
        try {
            mockAll();
            ResultActions request = TestUtils.performGet(mockMvc, "/userorganization/maermkdoiu/users");
            TestUtils.expectStatus(request, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getUsersOfOrganizationNoPage() {
        try {
            mockAll();
            ResultActions request = TestUtils.performGet(mockMvc, "/userorganization/" + userOrganization.getId() + "/users");
            TestUtils.expectStatus(request, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void promoteMember() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPost(mockMvc, null, "/userorganization/" + userOrganization.getId() + "/promote");
            TestUtils.expectStatus(request, HttpStatus.OK);
            UserOrganizationDto userOrganizationDto = mapper.readValue(TestUtils.getContentAsString(request), UserOrganizationDto.class);
            assertEquals(Role.ORGANIZATION_LEADER, userOrganizationDto.getRole());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void promoteMemberNotFound() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPost(mockMvc, null, "/userorganization/qdsmlfk/promote");
            TestUtils.expectStatus(request, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void promoteMemberWrongMethod() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPatch(mockMvc, null, "/userorganization/qdsmlfk/promote");
            TestUtils.expectStatus(request, HttpStatus.METHOD_NOT_ALLOWED);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void demoteMember() {
        try {
            mockAll();
            ResultActions request = TestUtils.performPost(mockMvc, null, "/userorganization/" + userOrganization.getId() + "/demote");
            TestUtils.expectStatus(request, HttpStatus.OK);
            UserOrganizationDto userOrganizationDto = mapper.readValue(TestUtils.getContentAsString(request), UserOrganizationDto.class);
            assertEquals(Role.MEMBER, userOrganizationDto.getRole());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getAllUserOrganizationsOfUser() {
        try {
            mockAll();
            ResultActions request = TestUtils.performGet(mockMvc, "/userorganization/user/" + user.getId());
            TestUtils.expectStatus(request, HttpStatus.OK);
            UserOrganizationsDto userOrganizationDto = mapper.readValue(TestUtils.getContentAsString(request), UserOrganizationsDto.class);
            assertNotNull(userOrganizationDto);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
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
        mockGetUserOrgsByUserId();
    }

    private void mockGetUserOrgsByUserId() {
        given(userOrganizationRepo.getAllUserOrganizationsByUserId(any())).will(i -> {
            List<UserOrganization> uos = new ArrayList<>();
            uos.add(userOrganization);
            return uos;
        });
    }

    private void mockSaves() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserByOrganizationAndStatus(any(), any(), any())).will(i -> {
            List<UserOrganization> pending = new ArrayList<>();
            pending.add(userOrganization);
            return new PageImpl<>(pending);
        });
    }

    private void mockUserOrganizationFetchById() {
        given(userOrganizationRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(userOrganization.getId())) {
                return Optional.of(userOrganization);
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