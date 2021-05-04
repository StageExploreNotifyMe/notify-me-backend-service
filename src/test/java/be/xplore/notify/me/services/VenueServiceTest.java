package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.entity.mappers.VenueEntityMapper;
import be.xplore.notify.me.repositories.VenueRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class VenueServiceTest {

    @Autowired
    private Venue venue;

    @MockBean
    private VenueRepo venueRepo;

    @Autowired
    private VenueService venueService;
    @Autowired
    private VenueEntityMapper venueEntityMapper;

    private void setUpMocks() {
        given(venueRepo.findById(any())).will(i -> venue.getId().equals(i.getArgument(0)) ? Optional.of(venueEntityMapper.toEntity(venue)) : Optional.empty());
        given(venueRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void getById() {
        setUpMocks();
        Optional<Venue> optional = venueService.getById(venue.getId());
        assertTrue(optional.isPresent());
        assertEquals(venue.getId(), optional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        setUpMocks();
        Optional<Venue> optional = venueService.getById("qdskjf");
        assertTrue(optional.isEmpty());
    }

    @Test
    void save() {
        setUpMocks();
        Venue savedVenue = venueService.save(venue);
        assertEquals(venue.getId(), savedVenue.getId());
    }
}