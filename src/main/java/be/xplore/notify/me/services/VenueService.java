package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.entity.mappers.VenueEntityMapper;
import be.xplore.notify.me.repositories.VenueRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class VenueService {

    private final VenueRepo repo;
    private final VenueEntityMapper entityMapper;

    public VenueService(VenueRepo repo, VenueEntityMapper entityMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
    }

    public Optional<Venue> getById(String id) {
        Optional<VenueEntity> optional = repo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Venue venue = entityMapper.fromEntity(optional.get());
        return Optional.of(venue);
    }

    public Venue save(Venue venue) {
        VenueEntity venueEntity = repo.save(entityMapper.toEntity(venue));
        return entityMapper.fromEntity(venueEntity);
    }
}
