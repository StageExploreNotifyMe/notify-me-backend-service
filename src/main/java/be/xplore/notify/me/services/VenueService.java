package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.entity.mappers.VenueEntityMapper;
import be.xplore.notify.me.repositories.VenueRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class VenueService {
    private final VenueRepo venueRepo;
    private final VenueEntityMapper venueEntityMapper;

    public VenueService(VenueRepo venueRepo, VenueEntityMapper venueEntityMapper) {
        this.venueRepo = venueRepo;
        this.venueEntityMapper = venueEntityMapper;
    }

    public Optional<Venue> getById(String id) {
        try {
            Optional<VenueEntity> optional = venueRepo.findById(id);
            if (optional.isEmpty()) {
                return Optional.empty();
            }
            Venue venue = venueEntityMapper.fromEntity(optional.get());
            return Optional.of(venue);
        } catch (Exception e) {
            log.error("Exception thrown while fetching venue with id {}: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public Venue save(Venue venue) {
        try {
            VenueEntity save = venueRepo.save(venueEntityMapper.toEntity(venue));
            return venueEntityMapper.fromEntity(save);
        } catch (Exception e) {
            log.error("Failed to save venue with id {}: {}: {}", venue.getId(), e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }
}
