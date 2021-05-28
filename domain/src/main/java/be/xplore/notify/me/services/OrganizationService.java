package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
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

    public Optional<Organization> findById(String id) {
        return organizationRepo.findById(id);
    }

    public Organization getById(String id) {
        Optional<Organization> byId = organizationRepo.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new NotFoundException("No organization with id " + id + " found");
    }

    public Optional<Organization> getOrganizationByName(String name) {
        return organizationRepo.findByName(name);
    }

    public Organization save(Organization organization) {
        return organizationRepo.save(organization);
    }

    public Organization updateOrganization(Organization toUpdate) {
        Optional<Organization> optional = findById(toUpdate.getId());
        if (optional.isEmpty()) {
            throw new NotFoundException("No organization with id " + toUpdate.getId() + " found.");
        }
        Organization updated = Organization.builder()
                .id(optional.get().getId())
                .name(toUpdate.getName())
                .build();
        return save(updated);
    }

    public Organization createOrganization(String name) {
        if (getOrganizationByName(name).isPresent()) {
            throw new AlreadyExistsException("An organization with the name " + name + " already exists");
        }

        Organization organization = Organization.builder().name(name).build();
        return save(organization);
    }
}
