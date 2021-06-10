package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private VenueService venueService;

    @Autowired
    private User user;

    @BeforeEach
    void setUp() {
        given(venueService.save(any())).will(i -> i.getArgument(0));
        given(venueService.getAllVenuesOfUser(any(), any(int.class))).willReturn(new PageImpl<>(new ArrayList<>()));
        given(venueService.getAllVenues(any(int.class))).willReturn(new PageImpl<>(new ArrayList<>()));
    }

    @Test
    void getVenues() {
        given(userService.getById(any())).willReturn(user);
        performGetVenue();
    }

    @Test
    void getVenuesNotAdmin() {
        given(userService.getById(any())).willReturn(User.builder().roles(new HashSet<>()).build());
        performGetVenue();
    }

    private void performGetVenue() {
        ResultActions request = TestUtils.performGet(mockMvc, "/venue/" + user.getId());
        TestUtils.expectStatus(request, HttpStatus.OK);
    }
}