package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaOrganizationRepo extends JpaRepository<OrganizationEntity, Long> {
    Optional<OrganizationEntity> findOrganizationEntityByName(String name);
}
