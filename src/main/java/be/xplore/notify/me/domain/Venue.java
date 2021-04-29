package be.xplore.notify.me.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Venue {
    String id;
    String name;
}
