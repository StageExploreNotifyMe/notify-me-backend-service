package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.VenueRepo;
import be.xplore.notify.me.services.user.UserService;
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
    private final UserService userService;

    public VenueService(VenueRepo repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public Optional<Venue> findById(String id) {
        return repo.findById(id);
    }

    public Venue getById(String id) {
        Optional<Venue> byId = findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new NotFoundException("No venue found for id " + id);
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

    public Venue addVenueManagerToVenue(Venue venue, List<User> users) {
        List<User> venueManagers = venue.getVenueManagers();
        venueManagers.addAll(users);
        users.forEach(u -> userService.addRole(u, Role.VENUE_MANAGER));
        return save(Venue.builder()
            .id(venue.getId())
            .name(venue.getName())
            .venueManagers(venueManagers)
            .lineManagers(venue.getLineManagers())
            .build());
    }

    public Venue updateVenue(Venue venue) {
        getById(venue.getId());
        return save(Venue.builder()
                .id(venue.getId())
                .name(venue.getName())
                .venueManagers(venue.getVenueManagers())
                .lineManagers(venue.getLineManagers())
                .build());
    }
}
