package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.UserOrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganizationRepo extends JpaRepository<UserOrganizationEntity, String> {
}
