package be.xplore.notify.me.util.mockadapters.user;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.persistence.UserOrganizationRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserOrganizationAdapter implements UserOrganizationRepo {

    @Override
    public Page<UserOrganization> getUserByOrganizationAndStatus(String organizationId, MemberRequestStatus status, PageRequest pageRequest) {
        return null;
    }

    @Override
    public List<UserOrganization> getAllOrganizationLeadersByOrganizationId(String organizationId) {
        return null;
    }

    @Override
    public UserOrganization save(UserOrganization userOrganization) {
        return null;
    }

    @Override
    public Optional<UserOrganization> findById(String userOrganizationId) {
        return Optional.empty();
    }

    @Override
    public List<UserOrganization> getAllUserOrganizationsByUserId(String userId) {
        return null;
    }
}
