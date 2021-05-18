package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
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

    public Organization save(Organization organization) {
        return organizationRepo.save(organization);
    }
}
