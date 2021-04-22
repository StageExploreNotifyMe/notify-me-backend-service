package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserOrganizationService {

    private final UserOrganizationRepo userOrganizationRepo;
    private final UserOrganizationNotificationService userOrganizationNotificationService;

    public UserOrganizationService(UserOrganizationRepo userOrganizationRepo, UserOrganizationNotificationService userOrganizationNotificationService) {
        this.userOrganizationRepo = userOrganizationRepo;
        this.userOrganizationNotificationService = userOrganizationNotificationService;
    }

    public UserOrganization userJoinOrganization(User user, Organization organization) {
        UserOrganization userOrganization = new UserOrganization(null, user, organization, Role.MEMBER, MemberRequestStatus.PENDING);
        return save(userOrganization);
    }

    public Page<UserOrganization> getPendingJoinRequests(String organizationId, PageRequest pageRequest) {
        try {
            return userOrganizationRepo.getUserOrganisationByOrganization_IdAndStatus(organizationId, MemberRequestStatus.PENDING, pageRequest);
        } catch (Exception e) {
            log.error("Fetching pending organisation join requests failed: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public UserOrganization resolvePendingJoinRequest(String requestId, boolean accepted) {
        Optional<UserOrganization> userOrganisationOptional = getById(requestId);
        if (userOrganisationOptional.isEmpty()) {
            throw new NotFoundException("No request with id " + requestId + " found");
        }
        UserOrganization userOrganization = userOrganisationOptional.get();

        MemberRequestStatus status = accepted ? MemberRequestStatus.ACCEPTED : MemberRequestStatus.DECLINED;
        userOrganization.setStatus(status);

        userOrganization = save(userOrganization);
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(userOrganization);
        return userOrganization;
    }

    public Optional<UserOrganization> getById(String id) {
        try {
            return userOrganizationRepo.findById(id);
        } catch (Exception e) {
            log.error("Fetching UserOrganisation with id {} failed: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public UserOrganization save(UserOrganization userOrganization) {
        try {
            return userOrganizationRepo.save(userOrganization);
        } catch (Exception e) {
            log.error("Saving UserOrganisation failed: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
