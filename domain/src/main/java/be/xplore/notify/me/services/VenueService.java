package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.VenueRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public Optional<Venue> findByName(String name) {
        return repo.findVenueEntityByName(name);
    }

    public Venue createVenue(String name) {
        if (findByName(name).isPresent()) {
            throw new AlreadyExistsException("A venue with name " + name + "already exists");
        }
        return save(Venue.builder().name(name).venueManagers(new ArrayList<>()).lineManagers(new ArrayList<>()).build());
    }

    public Venue addVenueManagerToVenue(Venue venue, User user) {
        List<User> venueManagers = venue.getVenueManagers();
        venueManagers.add(user);
        return save(Venue.builder().id(venue.getId()).name(venue.getName()).venueManagers(venueManagers).build());
    }
}
