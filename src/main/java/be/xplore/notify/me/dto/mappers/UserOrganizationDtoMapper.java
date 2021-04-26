package be.xplore.notify.me.dto.mappers;

import be.xplore.notify.me.domain.UserOrganization;
import be.xplore.notify.me.dto.UserOrganizationDto;
import org.springframework.stereotype.Component;

@Component
public class UserOrganizationDtoMapper implements DtoMapper<UserOrganizationDto, UserOrganization> {

    private final UserDtoMapper userDtoMapper;
    private final OrganizationDtoMapper organizationDtoMapper;

    public UserOrganizationDtoMapper(UserDtoMapper userDtoMapper, OrganizationDtoMapper organizationDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.organizationDtoMapper = organizationDtoMapper;
    }

    @Override
    public UserOrganization fromDto(UserOrganizationDto d) {
        return UserOrganization.builder()
            .id(d.getId())
            .role(d.getRole())
            .status(d.getStatus())
            .user(userDtoMapper.fromDto(d.getUser()))
            .organization(organizationDtoMapper.fromDto(d.getOrganization()))
            .build();
    }

    @Override
    public UserOrganizationDto toDto(UserOrganization d) {
        return new UserOrganizationDto(d.getId(), userDtoMapper.toDto(d.getUser()), organizationDtoMapper.toDto(d.getOrganization()), d.getRole(), d.getStatus());
    }
}
