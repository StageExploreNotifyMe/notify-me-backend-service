package be.xplore.notify.me.domain;

import be.xplore.notify.me.domain.user.User;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Venue {
    String id;
    String name;
    List<User> venueManagers;
    List<User> lineManagers;
}
