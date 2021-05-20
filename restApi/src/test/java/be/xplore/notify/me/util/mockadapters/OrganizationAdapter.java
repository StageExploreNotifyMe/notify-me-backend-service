package be.xplore.notify.me.util.mockadapters;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.persistence.OrganizationRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrganizationAdapter implements OrganizationRepo {

    @Override
    public Page<Organization> findAll(PageRequest of) {
        return null;
    }

    @Override
    public Organization save(Organization organization) {
        return null;
    }

    @Override
    public Optional<Organization> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<Organization> findByName(String name) {
        return Optional.empty();
    }
}
