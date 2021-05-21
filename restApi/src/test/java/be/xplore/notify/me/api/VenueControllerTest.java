package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.venue.CreateVenueDto;
import be.xplore.notify.me.services.VenueService;
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
class VenueControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Venue venue;

    @Autowired
    User user;

    @MockBean
    VenueService venueService;

    @MockBean
    UserService userService;

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
            ResultActions resultActions = mockMvc.perform(get("/admin/venue").contentType(MediaType.APPLICATION_JSON));
            resultActions.andExpect(status().is(HttpStatus.OK.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createVenue() {
        mockAll();
        mockGetUserById();
        CreateVenueDto createVenueDto = new CreateVenueDto("test venue", user.getId());
        try {
            String requestBody = mapper.writeValueAsString(createVenueDto);
            ResultActions resultActions = mockMvc.perform(post("/admin/venue/create").content(requestBody).contentType(MediaType.APPLICATION_JSON));
            resultActions.andExpect(status().is(HttpStatus.CREATED.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createVenueUserNotFound() {
        mockAll();
        CreateVenueDto createVenueDto = new CreateVenueDto("test venue", "sdfdq");
        try {
            String requestBody = mapper.writeValueAsString(createVenueDto);
            ResultActions resultActions = mockMvc.perform(post("/admin/venue/create").content(requestBody).contentType(MediaType.APPLICATION_JSON));
            resultActions.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void mockAll() {
        mockCreateVenue();
        mockGetVenues();
        mockAddVenueManager();
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

}