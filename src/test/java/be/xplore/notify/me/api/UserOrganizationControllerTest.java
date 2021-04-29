package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.user.UserOrganizationDto;
import be.xplore.notify.me.dto.user.UserOrganizationIdsDto;
import be.xplore.notify.me.dto.user.UserOrganizationProcessDto;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.entity.mappers.user.UserOrganizationEntityMapper;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import be.xplore.notify.me.repositories.OrganizationRepo;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import be.xplore.notify.me.repositories.UserRepo;
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
    private OrganizationEntityMapper organizationEntityMapper;
    @Autowired
    private UserEntityMapper userEntityMapper;
    @Autowired
    private UserOrganizationEntityMapper userOrganizationEntityMapper;

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
            String body = mapper.writeValueAsString(new UserOrganizationIdsDto(user.getId(), organization.getId()));
            String result = performRequestWithBody(body, HttpStatus.CREATED.value());
            UserOrganizationDto userOrganization = mapper.readValue(result, UserOrganizationDto.class);

            Assertions.assertEquals(user.getId(), userOrganization.getUser().getId());
            Assertions.assertEquals(organization.getId(), userOrganization.getOrganization().getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void mockAll() {
        mockFetchesByIds();
        mockSaves();
    }

    @Test
    void userJoinException() {
        try {
            mockAll();
            given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
            performRequestWithBody(mapper.writeValueAsString(new UserOrganizationIdsDto(user.getId(), organization.getId())), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void userJoinNotFound() {
        try {
            mockFetchesByIds();
            performJoinRequestWithBody(mapper.writeValueAsString(new UserOrganizationIdsDto("qqsdfqsdf", organization.getId())), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void userJoinNotFound2() {
        try {
            mockFetchesByIds();
            given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
            performJoinRequestWithBody(mapper.writeValueAsString(new UserOrganizationIdsDto(user.getId(), "sdfqqsdf")), HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void processJoinRequest() {
        try {
            mockAll();
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
            mockAll();
            String requestBody = mapper.writeValueAsString(new UserOrganizationProcessDto("qksdfj", true));
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
            mockFetchesByIds();
            mockUserOrganisationByOrganization_IdAndStatus();
            ResultActions request = mockMvc.perform(get("/userorganization/requests/1/pending/1").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void performJoinRequestWithBody(String requestBody, int status) throws Exception {
        ResultActions request = mockMvc.perform(post("/userorganization/request/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        ResultActions expectedRequest = request.andExpect(status().is(status));
        expectedRequest.andReturn().getResponse().getContentAsString();
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatus(any(), any(), any())).will(i -> {
            List<UserOrganizationEntity> pending = new ArrayList<>();
            pending.add(userOrganizationEntityMapper.toEntity(request));
            return new PageImpl<>(pending);
        });
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    private String performRequestWithBody(String requestBody, int status) throws Exception {
        ResultActions request = mockMvc.perform(post("/userorganization/request/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        ResultActions expectedRequest = request.andExpect(status().is(status));
        return expectedRequest.andReturn().getResponse().getContentAsString();
    }

    private void mockFetchesByIds() {
        mockOrganizationFetchById();
        mockUserFetchById();
        mockUserOrganizationFetchById();
    }

    private void mockSaves() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockUserOrganizationFetchById() {
        given(userOrganizationRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(request.getId())) {
                return Optional.of(userOrganizationEntityMapper.toEntity(request));
            }
            return Optional.empty();
        });
    }

    private void mockUserFetchById() {
        given(userRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return Optional.of(userEntityMapper.toEntity(user));
            }
            return Optional.empty();
        });
    }

    private void mockOrganizationFetchById() {
        given(organizationRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(organization.getId())) {
                return Optional.of(organizationEntityMapper.toEntity(organization));
            }
            return Optional.empty();
        });
    }
}