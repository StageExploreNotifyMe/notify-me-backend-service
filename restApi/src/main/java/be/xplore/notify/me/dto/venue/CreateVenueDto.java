package be.xplore.notify.me.dto.venue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVenueDto {
    private String name;
    private String venueManagerId;

}
