package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.persistence.VenueRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class VenueService {
    private final VenueRepo repo;

    public VenueService(VenueRepo repo) {
        this.repo = repo;
    }

    public Optional<Venue> getById(String id) {
        return repo.findById(id);
    }

    public Venue save(Venue venue) {
        return repo.save(venue);
    }

    public Page<Venue> getAllVenues(int page) {
        return repo.getAllVenues(PageRequest.of(page, 100));
    }
}
