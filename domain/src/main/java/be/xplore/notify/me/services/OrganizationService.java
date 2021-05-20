package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.persistence.OrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class OrganizationService {
    private final OrganizationRepo organizationRepo;

    public OrganizationService(OrganizationRepo organizationRepo) {
        this.organizationRepo = organizationRepo;
    }

    public Page<Organization> getOrganizations(int page) {
        return organizationRepo.findAll(PageRequest.of(page, 20));
    }

    public Optional<Organization> getById(String id) {
        return organizationRepo.findById(id);
    }

    public Optional<Organization> getOrganizationByName(String name) {
        return organizationRepo.findByName(name);
    }

    public Organization save(Organization organization) {
        return organizationRepo.save(organization);
    }

    public Organization createOrganization(String name) {
        if (getOrganizationByName(name).isPresent()) {
            throw new AlreadyExistsException("An organization with the name " + name + " already exists");
        }

        Organization organization = Organization.builder().name(name).build();
        return save(organization);
    }
}
