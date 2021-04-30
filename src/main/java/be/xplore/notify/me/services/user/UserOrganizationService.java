package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import be.xplore.notify.me.services.RepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserOrganizationService extends RepoService<UserOrganization, UserOrganizationEntity> {

    private final UserOrganizationRepo userOrganizationRepo;
    private final UserOrganizationNotificationService userOrganizationNotificationService;

    public UserOrganizationService(
            UserOrganizationRepo repo,
            EntityMapper<UserOrganizationEntity, UserOrganization> entityMapper,
            UserOrganizationNotificationService userOrganizationNotificationService
    ) {
        super(repo, entityMapper);
        this.userOrganizationRepo = repo;
        this.userOrganizationNotificationService = userOrganizationNotificationService;
    }

    public UserOrganization userJoinOrganization(User user, Organization organization) {
        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .role(Role.MEMBER)
                .status(MemberRequestStatus.PENDING)
                .build();
        return save(userOrganization);
    }

    public Page<UserOrganization> getAllUsersByOrganizationId(String organizationId, PageRequest pageRequest) {
        return getUserByOrganizationAndStatus(organizationId, pageRequest, MemberRequestStatus.ACCEPTED);
    }

    public Page<UserOrganization> getPendingJoinRequests(String organizationId, PageRequest pageRequest) {
        return getUserByOrganizationAndStatus(organizationId, pageRequest, MemberRequestStatus.PENDING);
    }

    private Page<UserOrganization> getUserByOrganizationAndStatus(String organizationId, PageRequest pageRequest, MemberRequestStatus status) {
        try {
            Page<UserOrganizationEntity> userOrganisationPage =
                    userOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatusOrderByUserEntity(organizationId, status, pageRequest);
            return userOrganisationPage.map(entityMapper::fromEntity);
        } catch (Exception e) {
            log.error("Fetching UserOrganizations failed: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public UserOrganization resolvePendingJoinRequest(String requestId, boolean accepted) {
        Optional<UserOrganization> userOrganisationOptional = getById(requestId);
        if (userOrganisationOptional.isEmpty()) {
            throw new NotFoundException("No request with id " + requestId + " found");
        }
        UserOrganization request = userOrganisationOptional.get();

        MemberRequestStatus status = accepted ? MemberRequestStatus.ACCEPTED : MemberRequestStatus.DECLINED;
        request = UserOrganization.builder().id(request.getId()).role(request.getRole()).status(status).organization(request.getOrganization()).user(request.getUser()).build();
        request = save(request);
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(request);
        return request;
    }

    public UserOrganization changeOrganizationMemberRole(UserOrganization userOrganization, Role roleToChangeTo) {
        if (userOrganization.getRole().equals(roleToChangeTo)) {
            return userOrganization;
        }

        UserOrganization updated = UserOrganization.builder()
                .id(userOrganization.getId())
                .status(userOrganization.getStatus())
                .organization(userOrganization.getOrganization())
                .user(userOrganization.getUser())
                .role(roleToChangeTo)
                .build();

        return save(updated);
    }

}
