package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.repositories.OrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrganizationService extends RepoService<Organization, OrganizationEntity> {

    private final OrganizationRepo organizationRepo;
    private final OrganizationEntityMapper organizationEntityMapper;

    public OrganizationService(OrganizationRepo repo, OrganizationEntityMapper entityMapper) {
        super(repo, entityMapper);
        this.organizationRepo = repo;
        this.organizationEntityMapper = entityMapper;
    }

    public Page<Organization> getOrganizations(int page) {
        Page<OrganizationEntity> organizationEntityPage = organizationRepo.findAll(PageRequest.of(page, 20));
        return organizationEntityPage.map(organizationEntityMapper::fromEntity);
    }
}
