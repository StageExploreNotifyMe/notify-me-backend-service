package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.repositories.OrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final OrganizationEntityMapper organizationEntityMapper;

    public OrganizationService(OrganizationRepo organizationRepo, OrganizationEntityMapper organizationEntityMapper) {
        this.organizationRepo = organizationRepo;
        this.organizationEntityMapper = organizationEntityMapper;
    }

    public Page<Organization> getOrganizations(int page) {
        Page<OrganizationEntity> organizationEntityPage = organizationRepo.findAll(PageRequest.of(page, 20));
        return organizationEntityPage.map(organizationEntityMapper::fromEntity);
    }

    public Optional<Organization> getById(String id) {
        Optional<OrganizationEntity> optional = organizationRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Organization organization = organizationEntityMapper.fromEntity(optional.get());
        return Optional.of(organization);
    }

    public Organization save(Organization organization) {
        OrganizationEntity organizationEntity = organizationRepo.save(organizationEntityMapper.toEntity(organization));
        return organizationEntityMapper.fromEntity(organizationEntity);
    }
}
