package be.xplore.notify.me.entity.mappers;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEntityMapper implements EntityMapper<OrganizationEntity, Organization> {
    @Override
    public Organization fromEntity(OrganizationEntity organizationEntity) {
        return Organization.builder().id(organizationEntity.getId()).name(organizationEntity.getName()).build();
    }

    @Override
    public OrganizationEntity toEntity(Organization organization) {
        return new OrganizationEntity(organization.getId(), organization.getName());
    }
}
