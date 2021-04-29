package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepo extends JpaRepository<OrganizationEntity, String> {
}
