package be.xplore.notify.me.dto;

import be.xplore.notify.me.domain.MemberRequestStatus;
import be.xplore.notify.me.domain.Role;
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
    private UserDto user;
    private OrganizationDto organization;
    private Role role;
    private MemberRequestStatus status;
}
