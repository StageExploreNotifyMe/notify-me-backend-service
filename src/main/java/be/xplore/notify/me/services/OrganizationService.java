package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.repositories.OrganizationRepo;
import lombok.extern.slf4j.Slf4j;
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

    public Optional<Organization> getById(String id) {
        try {
            Optional<OrganizationEntity> optional = organizationRepo.findById(id);
            if (optional.isEmpty()) {
                return Optional.empty();
            }
            Organization organization = organizationEntityMapper.fromEntity(optional.get());
            return Optional.of(organization);
        } catch (Exception e) {
            log.error("Exception thrown while fetching organization with id {}: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }
}
