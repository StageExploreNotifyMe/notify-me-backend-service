package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.Venue;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenueRepo {
    Venue save(Venue venue);

    Optional<Venue> findById(String id);
}
