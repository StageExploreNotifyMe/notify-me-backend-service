package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, String> {
}
