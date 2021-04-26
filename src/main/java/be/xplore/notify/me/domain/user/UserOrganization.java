package be.xplore.notify.me.domain.user;

import be.xplore.notify.me.domain.Organization;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserOrganization {
    String id;
    User user;
    Organization organization;
    Role role;
    MemberRequestStatus status;

}
