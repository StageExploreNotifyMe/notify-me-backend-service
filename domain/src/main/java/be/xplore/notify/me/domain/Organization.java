package be.xplore.notify.me.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Organization {
    String id;
    String name;
}
