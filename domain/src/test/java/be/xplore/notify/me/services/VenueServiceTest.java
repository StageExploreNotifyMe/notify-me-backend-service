package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.VenueRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class VenueServiceTest {

    @Autowired
    private Venue venue;

    @Autowired
    private User user;

    @MockBean
    private VenueRepo venueRepo;

    @Autowired
    private VenueService venueService;

    private void setUpMocks() {
        given(venueRepo.findById(any())).will(i -> venue.getId().equals(i.getArgument(0)) ? Optional.of(venue) : Optional.empty());
        given(venueRepo.save(any())).will(i -> i.getArgument(0));
        given(venueRepo.getAllVenues(any())).will(i -> getPageOfVenue());
    }

    private void mockGetByName() {
        given(venueRepo.findVenueEntityByName(any())).willReturn(Optional.of(venue));
    }

    private Object getPageOfVenue() {
        List<Venue> venueList = new ArrayList<>();
        venueList.add(venue);
        return new PageImpl<>(venueList);
    }

    @Test
    void getById() {
        setUpMocks();
        Optional<Venue> optional = venueService.findById(venue.getId());
        assertTrue(optional.isPresent());
        assertEquals(venue.getId(), optional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        setUpMocks();
        Optional<Venue> optional = venueService.findById("qdskjf");
        assertTrue(optional.isEmpty());
    }

    @Test
    void save() {
        setUpMocks();
        Venue savedVenue = venueService.save(venue);
        assertEquals(venue.getId(), savedVenue.getId());
    }

    @Test
    void getAllVenues() {
        setUpMocks();
        Page<Venue> venues = venueService.getAllVenues(0);
        assertEquals(1, venues.getContent().size());
    }

    @Test
    void createEvent() {
        setUpMocks();
        String venueName = "Test Venue";
        Venue venue = venueService.createVenue(venueName);
        assertEquals(venue.getName(), venueName);
    }

    @Test
    void createEventNameAlreadyExists() {
        setUpMocks();
        mockGetByName();
        assertThrows(AlreadyExistsException.class, () -> venueService.createVenue(venue.getName()));
    }

    @Test
    void addVenueManager() {
        setUpMocks();
        List<User> users = new ArrayList<>();
        users.add(user);
        Venue v = venueService.addVenueManagerToVenue(venue, users);
        assertEquals(v.getId(), venue.getId());
        assertTrue(v.getVenueManagers().contains(user));
    }

    @Test
    void updateVenue() {
        setUpMocks();
        String name = "updated name";
        Venue updatedVenue = venueService.updateVenue(Venue.builder().id(venue.getId()).name(name).build());
        assertEquals(name, updatedVenue.getName());
    }

    @Test
    void updateVenueNotFound() {
        given(venueRepo.findById(any())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> venueService.updateVenue(venueService.updateVenue(Venue.builder().id(venue.getId()).name("name").build())));
    }
}