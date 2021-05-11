package be.xplore.notify.me.dto.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.dto.VenueDto;
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
