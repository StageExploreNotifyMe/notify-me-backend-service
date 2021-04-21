package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.UserOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganisationRepo extends JpaRepository<UserOrganisation, String> {
}
