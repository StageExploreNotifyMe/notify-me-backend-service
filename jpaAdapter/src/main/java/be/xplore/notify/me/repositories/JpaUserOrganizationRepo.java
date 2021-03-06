package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaUserOrganizationRepo extends JpaRepository<UserOrganizationEntity, Long> {
    Page<UserOrganizationEntity> getUserOrganisationByOrganizationEntity_IdAndStatusOrderByUserEntity(long organizationEntity_id, MemberRequestStatus status, Pageable pageable);

    List<UserOrganizationEntity> getUserOrganizationEntityByOrganizationEntity_Id(long organizationEntity_id);

    List<UserOrganizationEntity> getUserOrganizationEntitiesByUserEntity_Id(long userEntity_id);
}
