package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.dto.venue.VenueDto;
import org.springframework.stereotype.Component;

@Component
public class VenueDtoMapper implements DtoMapper<VenueDto, Venue> {
    @Override
    public Venue fromDto(VenueDto d) {
        return Venue.builder().id(d.getId()).name(d.getName()).build();
    }

    @Override
    public VenueDto toDto(Venue d) {
        return new VenueDto(d.getId(), d.getName());
    }
}
