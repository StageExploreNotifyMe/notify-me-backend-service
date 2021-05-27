package be.xplore.notify.me.dto.venue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVenueDto {
    private String name;
    private List<String> venueManagerIds;

}
