package be.xplore.notify.me.domain.event;

import be.xplore.notify.me.domain.Venue;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Event {
    String id;
    String name;
    LocalDateTime date;
    EventStatus eventStatus;
    Venue venue;
}
