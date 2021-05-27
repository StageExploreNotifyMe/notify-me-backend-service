package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.dto.venue.VenueDto;
import be.xplore.notify.me.mappers.user.UserDtoMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class VenueDtoMapper implements DtoMapper<VenueDto, Venue> {
    private final UserDtoMapper userDtoMapper;

    public VenueDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public Venue fromDto(VenueDto d) {
        return Venue.builder().id(d.getId()).name(d.getName()).venueManagers(d.getVenueManagers().stream().map(userDtoMapper::fromDto).collect(Collectors.toList())).build();
    }

    @Override
    public VenueDto toDto(Venue d) {
        return new VenueDto(d.getId(), d.getName(), d.getVenueManagers().stream().map(userDtoMapper::toDto).collect(Collectors.toList()));
    }
}
