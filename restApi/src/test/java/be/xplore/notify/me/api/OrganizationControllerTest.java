package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.organization.CreateOrganizationDto;
import be.xplore.notify.me.dto.organization.OrganizationDto;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import be.xplore.notify.me.services.user.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class OrganizationControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserOrganizationService userOrganizationService;

    @Autowired
    private Organization organization;
    @Autowired
    private User user;
    @Autowired
    private UserOrganization userOrganization;

    @Test
    void getOrganizationById() {
        try {
            mockFetchById();
            ResultActions request = performGetAndExpect("/organization/" + organization.getId(), HttpStatus.OK);
            OrganizationDto returnedOrg = mapper.readValue(TestUtils.getContentAsString(request), OrganizationDto.class);
            assertEquals(organization.getId(), returnedOrg.getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getOrganizationByIdExpectNotFound() {
        try {
            mockFetchById();
            performGetAndExpect("/organization/" + organization.getId() + "qds", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getOrganizations() {
        try {
            mockFetchAll();
            performGetAndExpect("/organization", HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getOrganizationsWithPage() {
        try {
            mockFetchAll();
            performGetAndExpect("/organization?page=0", HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createOrganizations() {
        try {
            mockGetUserById();
            mockCreateOrg();
            mockAddUserOrgLeader();
            performPostAndExpect("/organization/create", HttpStatus.OK, new CreateOrganizationDto("testName", user.getId()));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createOrganizationsUserNotFound() {
        try {
            mockGetUserById();
            mockCreateOrg();
            mockAddUserOrgLeader();
            performPostAndExpect("/organization/create", HttpStatus.NOT_FOUND, new CreateOrganizationDto("testName", "qsdf"));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createOrganizationsAlreadyExists() {
        try {
            mockGetUserById();
            mockAddUserOrgLeader();
            given(organizationService.createOrganization(any())).willThrow(AlreadyExistsException.class);
            performPostAndExpect("/organization/create", HttpStatus.CONFLICT, new CreateOrganizationDto("testName", user.getId()));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void updateOrganization() {
        try {
            mockUpdateOrganization();
            ResultActions request = TestUtils.performPatch(mockMvc, organization, "/organization");
            TestUtils.expectStatus(request, HttpStatus.OK);
            OrganizationDto returnedOrg = mapper.readValue(TestUtils.getContentAsString(request), OrganizationDto.class);
            assertEquals(organization.getId(), returnedOrg.getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void performPostAndExpect(String url, HttpStatus status, Object dto) throws Exception {
        ResultActions perform = TestUtils.performPost(mockMvc, dto, url);
        TestUtils.expectStatus(perform, status);
    }

    private ResultActions performGetAndExpect(String url, HttpStatus status) throws Exception {
        ResultActions perform = TestUtils.performGet(mockMvc, url);
        TestUtils.expectStatus(perform, status);
        return perform;
    }

    private void mockFetchById() {
        given(organizationService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(organization.getId())) {
                return organization;
            }
            throw new NotFoundException("");
        });
    }

    private void mockFetchAll() {
        given(organizationService.getOrganizations(any(int.class))).will(i -> {
            List<Organization> organizations = new ArrayList<>();
            organizations.add(organization);
            return new PageImpl<>(organizations);
        });
    }

    private void mockUpdateOrganization() {
        given(organizationService.updateOrganization(any())).will(i -> i.getArgument(0));
    }

    private void mockAddUserOrgLeader() {
        given(userOrganizationService.addOrganizationLeaderToOrganization(any(), any())).willReturn(userOrganization);
    }

    private void mockCreateOrg() {
        given(organizationService.createOrganization(any())).willReturn(organization);
    }

    private void mockGetUserById() {
        given(userService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return user;
            }
            throw new NotFoundException("");
        });
    }
}