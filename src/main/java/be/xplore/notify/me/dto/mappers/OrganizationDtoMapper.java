package be.xplore.notify.me.dto.mappers;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.dto.OrganizationDto;
import org.springframework.stereotype.Component;

@Component
public class OrganizationDtoMapper implements DtoMapper<OrganizationDto, Organization> {
    @Override
    public Organization fromDto(OrganizationDto d) {
        return Organization.builder().id(d.getId()).build();
    }

    @Override
    public OrganizationDto toDto(Organization d) {
        return new OrganizationDto(d.getId(), d.getName());
    }
}
