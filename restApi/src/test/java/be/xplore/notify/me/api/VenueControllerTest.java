package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.venue.CreateVenueDto;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.TestUtils;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Venue venue;
    @Autowired
    private User user;

    @MockBean
    private VenueService venueService;
    @MockBean
    private UserService userService;

    private void mockGetVenues() {
        given(venueService.getAllVenues(any(int.class))).will(i -> {
            List<Venue> venueList = new ArrayList<>();
            venueList.add(venue);
            return new PageImpl<>(venueList);
        });
    }

    private void mockCreateVenue() {
        given(venueService.createVenue(any(String.class))).will(i -> Venue.builder().name("Test Venue").build());
    }

    private void mockAddVenueManager() {
        given(venueService.addVenueManagerToVenue(any(), any())).willReturn(venue);
    }

    private void mockGetUserById() {
        given(userService.getById(any())).willReturn(Optional.of(user));
    }

    @Test
    void getVenues() {
        try {
            mockGetVenues();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/admin/venue");
            resultActions.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createVenue() {
        mockAll();
        mockGetUserById();
        CreateVenueDto createVenueDto = new CreateVenueDto("test venue", user.getId());
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, createVenueDto, "/admin/venue/create");
            TestUtils.expectStatus(resultActions, HttpStatus.CREATED);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createVenueUserNotFound() {
        mockAll();
        CreateVenueDto createVenueDto = new CreateVenueDto("test venue", "sdfdq");
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, createVenueDto, "/admin/venue/create");
            TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockAll() {
        mockCreateVenue();
        mockGetVenues();
        mockAddVenueManager();
    }

}