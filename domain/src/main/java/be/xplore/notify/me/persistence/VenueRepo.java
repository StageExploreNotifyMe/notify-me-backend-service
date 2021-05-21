package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenueRepo {
    Venue save(Venue venue);

    Optional<Venue> findById(String id);

    Page<Venue> getAllVenues(PageRequest pageRequest);

    Optional<Venue> findVenueEntityByName(String name);

}
