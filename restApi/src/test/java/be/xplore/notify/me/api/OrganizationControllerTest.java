package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.dto.OrganizationDto;
import be.xplore.notify.me.persistence.OrganizationRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrganizationControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationRepo organizationRepo;
    @Autowired
    private Organization organization;

    @Test
    void getOrganizationById() {
        try {
            mockFetchByIds();
            ResultActions request = mockMvc.perform(get("/organization/" + organization.getId()).contentType(MediaType.APPLICATION_JSON));
            ResultActions expectedRequest = request.andExpect(status().is(HttpStatus.OK.value()));
            String contentAsString = expectedRequest.andReturn().getResponse().getContentAsString();
            OrganizationDto returnedOrg = mapper.readValue(contentAsString, OrganizationDto.class);
            assertEquals(organization.getId(), returnedOrg.getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getOrganizationByIdExpectNotFound() {
        try {
            mockFetchByIds();
            ResultActions request = mockMvc.perform(get("/organization/" + organization.getId() + "qds").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getOrganizations() {
        try {
            mockFetchAll();
            ResultActions request = mockMvc.perform(get("/organization").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getOrganizationsWithPage() {
        try {
            mockFetchAll();
            ResultActions request = mockMvc.perform(get("/organization?page=0").contentType(MediaType.APPLICATION_JSON));
            request.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    private void mockFetchByIds() {
        given(organizationRepo.findById(any())).will(i -> {
            if (i.getArgument(0).equals(organization.getId())) {
                return Optional.of(organization);
            }
            return Optional.empty();
        });
    }

    private void mockFetchAll() {
        given(organizationRepo.findAll(any(PageRequest.class))).will(i -> {
            List<Organization> organizations = new ArrayList<>();
            organizations.add(organization);
            return new PageImpl<>(organizations);
        });
    }
}