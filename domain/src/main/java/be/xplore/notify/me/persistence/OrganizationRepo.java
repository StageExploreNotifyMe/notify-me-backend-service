package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepo {
    Page<Organization> findAll(PageRequest of);

    Organization save(Organization organization);

    Optional<Organization> findById(String id);
}
