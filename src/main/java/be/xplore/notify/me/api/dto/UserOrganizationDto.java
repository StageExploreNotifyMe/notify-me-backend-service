package be.xplore.notify.me.api.dto;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationDto {
    private String id;
    private Role role;
    private MemberRequestStatus status;
    private UserDto user;
    private OrganizationDto organization;

}
