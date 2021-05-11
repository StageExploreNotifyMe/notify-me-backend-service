package be.xplore.notify.me.adapters;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.mappers.VenueEntityMapper;
import be.xplore.notify.me.persistence.VenueRepo;
import be.xplore.notify.me.repositories.JpaVenueRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class VenueAdapter implements VenueRepo {

    private final JpaVenueRepo repo;
    private final VenueEntityMapper entityMapper;

    public VenueAdapter(JpaVenueRepo repo, VenueEntityMapper entityMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
    }

    public Optional<Venue> findById(String id) {
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
