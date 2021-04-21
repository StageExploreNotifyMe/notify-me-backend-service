package be.xplore.notify.me.api;

import be.xplore.notify.me.api.dto.UserOrganizationIdsDto;
import be.xplore.notify.me.domain.UserOrganisation;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.repositories.UserOrganisationRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserOrganisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final String userId = "testUser";
    private final String organizationId = "testOrganization";

    @MockBean
    private UserOrganisationRepo userOrganisationRepo;

    @BeforeEach
    void setUp() {
        given(userOrganisationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void userJoinOrganization() {
        try {
            String result = performRequestWithBody(mapper.writeValueAsString(new UserOrganizationIdsDto(userId, organizationId)), HttpStatus.CREATED.value());
            UserOrganisation userOrganisation = mapper.readValue(result, UserOrganisation.class);

            Assertions.assertEquals(userId, userOrganisation.getUser().getId());
            Assertions.assertEquals(organizationId, userOrganisation.getOrganization().getId());
        } catch (Exception e) {
            FailTest(e);
        }
    }

    @Test
    void userJoinException() {
        try {
            given(userOrganisationRepo.save(any())).willThrow(new DatabaseException(new Exception()));
            performRequestWithBody(mapper.writeValueAsString(new UserOrganizationIdsDto(userId, organizationId)), HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e) {
            FailTest(e);
        }
    }

    private void FailTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    private String performRequestWithBody(String requestBody, int status) throws Exception {
        ResultActions request = mockMvc.perform(post("/userorganisation/request/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        ResultActions expectedRequest = request.andExpect(status().is(status));
        return expectedRequest.andReturn().getResponse().getContentAsString();
    }
}