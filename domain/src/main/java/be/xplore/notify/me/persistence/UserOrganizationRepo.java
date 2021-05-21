package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.UserOrganization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrganizationRepo {

    Page<UserOrganization> getUserByOrganizationAndStatus(String organizationId, MemberRequestStatus status, PageRequest pageRequest);

    List<UserOrganization> getAllOrganizationLeadersByOrganizationId(String organizationId);

    UserOrganization save(UserOrganization userOrganization);

    Optional<UserOrganization> findById(String userOrganizationId);

    List<UserOrganization> getAllUserOrganizationsByUserId(String userId);
}
