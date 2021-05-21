package be.xplore.notify.me.dto.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineCreationDto {
    private String name;
    private String description;
    private String venueId;
    private int numberOfRequiredPeople;
}
