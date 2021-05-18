package be.xplore.notify.me.dto.event;

import be.xplore.notify.me.dto.VenueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineDto {
    private String id;
    private String name;
    private String description;
    private VenueDto venueDto;
    private int numberOfRequiredPeople;
}
