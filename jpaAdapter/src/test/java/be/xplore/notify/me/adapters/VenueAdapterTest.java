package be.xplore.notify.me.adapters;

import be.xplore.notify.me.domain.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class VenueAdapterTest {

    @Autowired
    private VenueAdapter venueAdapter;

    @Test
    void findById() {
        Optional<Venue> venue = venueAdapter.findById("1");
        assertTrue(venue.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<Venue> venue = venueAdapter.findById("qsdkfljl");
        assertTrue(venue.isEmpty());
    }

    @Test
    void save() {
        Venue venue = Venue.builder().name("Test").build();
        Venue saved = venueAdapter.save(venue);
        assertEquals(venue.getName(), saved.getName());
    }

    @Test
    void getAllVenues() {
        Page<Venue> venues = venueAdapter.getAllVenues(PageRequest.of(0, 20));
        assertTrue(venues.hasContent());
    }

    @Test
    void findVenueEntityByName() {
        Optional<Venue> optional = venueAdapter.findVenueEntityByName("Groenplaats");
        assertTrue(optional.isPresent());
    }

    @Test
    void findVenueEntityByNameNotFound() {
        Optional<Venue> optional = venueAdapter.findVenueEntityByName("sdfqs");
        assertTrue(optional.isEmpty());
    }
}