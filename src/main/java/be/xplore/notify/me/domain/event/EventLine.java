package be.xplore.notify.me.domain.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.user.User;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EventLine {
    String id;
    Line line;
    Event event;
    Organization organization;
    List<User> assignedUsers;
    User lineManager;
}
