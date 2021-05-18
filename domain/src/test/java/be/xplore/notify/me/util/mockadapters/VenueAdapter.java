package be.xplore.notify.me.util.mockadapters;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.persistence.VenueRepo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VenueAdapter implements VenueRepo {

    @Override
    public Venue save(Venue venue) {
        return null;
    }

    @Override
    public Optional<Venue> findById(String id) {
        return Optional.empty();
    }
}
