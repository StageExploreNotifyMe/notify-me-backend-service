package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.repositories.OrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class OrganizationService {

    private final OrganizationRepo organizationRepo;

    public OrganizationService(OrganizationRepo organizationRepo) {
        this.organizationRepo = organizationRepo;
    }

    public Optional<Organization> getById(String id) {
        try {
            return organizationRepo.findById(id);
        } catch (Exception e) {
            log.error("Exception thrown while fetching organization with id {}: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }
}
