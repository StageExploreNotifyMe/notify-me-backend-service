package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.organization.CreateOrganizationDto;
import be.xplore.notify.me.dto.organization.OrganizationDto;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
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
            OrganizationDto returnedOrg = mapper.readValue(andReturn(request), OrganizationDto.class);
            assertEquals(organization.getId(), returnedOrg.getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getOrganizationByIdExpectNotFound() {
        try {
            mockFetchById();
            performGetAndExpect("/organization/" + organization.getId() + "qds", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getOrganizations() {
        try {
            mockFetchAll();
            performGetAndExpect("/organization", HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getOrganizationsWithPage() {
        try {
            mockFetchAll();
            performGetAndExpect("/organization?page=0", HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
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
            failTest(e);
        }
    }

    @Test
    void createOrganizationsUserNotFound() {
        try {
            given(userService.getById(any())).willReturn(Optional.empty());
            mockCreateOrg();
            mockAddUserOrgLeader();
            performPostAndExpect("/organization/create", HttpStatus.NOT_FOUND, new CreateOrganizationDto("testName", user.getId()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createOrganizationsAlreadyExists() {
        try {
            mockGetUserById();
            mockAddUserOrgLeader();
            given(organizationService.createOrganization(any())).willThrow(AlreadyExistsException.class);
            ResultActions request = performPostAndExpect("/organization/create", HttpStatus.CONFLICT, new CreateOrganizationDto("testName", user.getId()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void updateOrganization() {
        try {
            mockUpdateOrganization();
            ResultActions request = mockMvc.perform(patch("/organization").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(organization)));
            expect(request, HttpStatus.OK);
            OrganizationDto returnedOrg = mapper.readValue(andReturn(request), OrganizationDto.class);
            assertEquals(organization.getId(), returnedOrg.getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    private ResultActions performPostAndExpect(String url, HttpStatus status, Object dto) throws Exception {
        ResultActions perform = mockMvc.perform(post(url).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        expect(perform, status);
        return perform;
    }

    private ResultActions performGetAndExpect(String url, HttpStatus status) throws Exception {
        ResultActions request = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON));
        expect(request, status);
        return request;
    }

    private void expect(ResultActions request, HttpStatus status) throws Exception {
        request.andExpect(status().is(status.value()));
    }

    private String andReturn(ResultActions request) throws UnsupportedEncodingException {
        return request.andReturn().getResponse().getContentAsString();
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    private void mockFetchById() {
        given(organizationService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(organization.getId())) {
                return Optional.of(organization);
            }
            return Optional.empty();
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
        given(userService.getById(any())).willReturn(Optional.of(user));
    }
}