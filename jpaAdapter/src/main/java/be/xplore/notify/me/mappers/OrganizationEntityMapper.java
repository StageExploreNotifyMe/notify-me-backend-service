package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEntityMapper implements EntityMapper<OrganizationEntity, Organization> {
    @Override
    public Organization fromEntity(OrganizationEntity organizationEntity) {
        if (organizationEntity == null) {
            return null;
        }
        return Organization.builder().id(String.valueOf(organizationEntity.getId())).name(organizationEntity.getName()).build();
    }

    @Override
    public OrganizationEntity toEntity(Organization organization) {
        if (organization == null) {
            return null;
        }
        return new OrganizationEntity(LongParser.parseLong(organization.getId()), organization.getName());
    }
}
