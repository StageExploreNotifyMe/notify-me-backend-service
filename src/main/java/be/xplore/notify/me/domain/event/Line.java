package be.xplore.notify.me.domain.event;

import be.xplore.notify.me.domain.Venue;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Line {
    String id;
    String name;
    String description;
    Venue venue;
}
