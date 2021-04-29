package be.xplore.notify.me.entity.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.entity.VenueEntity;
import org.springframework.stereotype.Component;

@Component
public class VenueEntityMapper implements EntityMapper<VenueEntity, Venue> {
    @Override
    public Venue fromEntity(VenueEntity venueEntity) {
        return Venue.builder().id(venueEntity.getId()).name(venueEntity.getName()).build();
    }

    @Override
    public VenueEntity toEntity(Venue venue) {
        return new VenueEntity(venue.getId(), venue.getName());
    }
}
