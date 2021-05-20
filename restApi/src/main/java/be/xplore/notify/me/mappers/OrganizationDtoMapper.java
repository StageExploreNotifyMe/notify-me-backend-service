package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.dto.organization.OrganizationDto;
import org.springframework.stereotype.Component;

@Component
public class OrganizationDtoMapper implements DtoMapper<OrganizationDto, Organization> {
    @Override
    public Organization fromDto(OrganizationDto d) {
        if (d == null) {
            return null;
        }
        return Organization.builder().id(d.getId()).build();
    }

    @Override
    public OrganizationDto toDto(Organization d) {
        if (d == null) {
            return null;
        }
        return new OrganizationDto(d.getId(), d.getName());
    }
}
