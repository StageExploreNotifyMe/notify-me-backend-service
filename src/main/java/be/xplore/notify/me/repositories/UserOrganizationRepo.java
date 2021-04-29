package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganizationRepo extends JpaRepository<UserOrganizationEntity, String> {
    Page<UserOrganizationEntity> getUserOrganisationByOrganizationEntity_IdAndStatus(String organizationEntity_id, MemberRequestStatus status, Pageable pageable);
}
