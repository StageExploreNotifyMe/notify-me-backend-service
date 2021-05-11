package be.xplore.notify.me.adapters;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.persistence.OrganizationRepo;
import be.xplore.notify.me.repositories.JpaOrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class OrganizationAdapter implements OrganizationRepo {

    private final JpaOrganizationRepo jpaOrganizationRepo;
    private final OrganizationEntityMapper organizationEntityMapper;

    public OrganizationAdapter(JpaOrganizationRepo jpaOrganizationRepo, OrganizationEntityMapper organizationEntityMapper) {
        this.jpaOrganizationRepo = jpaOrganizationRepo;
        this.organizationEntityMapper = organizationEntityMapper;
    }

    @Override
    public Page<Organization> findAll(PageRequest pageRequest) {
        Page<OrganizationEntity> organizationEntityPage = jpaOrganizationRepo.findAll(pageRequest);
        return organizationEntityPage.map(organizationEntityMapper::fromEntity);
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationEntity organizationEntity = jpaOrganizationRepo.save(organizationEntityMapper.toEntity(organization));
        return organizationEntityMapper.fromEntity(organizationEntity);
    }

    @Override
    public Optional<Organization> findById(String id) {
        Optional<OrganizationEntity> optional = jpaOrganizationRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Organization organization = organizationEntityMapper.fromEntity(optional.get());
        return Optional.of(organization);
    }
}
