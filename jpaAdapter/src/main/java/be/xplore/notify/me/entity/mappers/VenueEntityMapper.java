package be.xplore.notify.me.entity.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.entity.mappers.user.UserEntityMapper;
import be.xplore.notify.me.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VenueEntityMapper implements EntityMapper<VenueEntity, Venue> {

    private final UserEntityMapper userEntityMapper;

    public VenueEntityMapper(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public Venue fromEntity(VenueEntity venueEntity) {
        List<User> venueManagers = new ArrayList<>();
        if (venueEntity.getVenueManagers() != null) {
            venueManagers = venueEntity.getVenueManagers().stream().map(userEntityMapper::fromEntity).collect(Collectors.toList());
        }
        List<User> lineManagers = new ArrayList<>();
        if (venueEntity.getLineManagers() != null) {
            lineManagers = venueEntity.getLineManagers().stream().map(userEntityMapper::fromEntity).collect(Collectors.toList());
        }
        return Venue.builder().id(venueEntity.getId()).name(venueEntity.getName()).venueManagers(venueManagers).lineManagers(lineManagers).build();
    }

    @Override
    public VenueEntity toEntity(Venue venue) {
        List<UserEntity> venueManagers = new ArrayList<>();
        if (venue.getVenueManagers() != null) {
            venueManagers = venue.getVenueManagers().stream().map(userEntityMapper::toEntity).collect(Collectors.toList());
        }

        List<UserEntity> lineManagers = new ArrayList<>();
        if (venue.getVenueManagers() != null) {
            lineManagers = venue.getLineManagers().stream().map(userEntityMapper::toEntity).collect(Collectors.toList());
        }
        return new VenueEntity(venue.getId(), venue.getName(), venueManagers, lineManagers);
    }
}
