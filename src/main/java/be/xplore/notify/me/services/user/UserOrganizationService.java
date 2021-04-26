package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.entity.mappers.user.UserOrganizationEntityMapper;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
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
    private final UserOrganizationEntityMapper userOrganizationEntityMapper;

    public UserOrganizationService(
            UserOrganizationRepo userOrganizationRepo,
            UserOrganizationNotificationService userOrganizationNotificationService,
            UserOrganizationEntityMapper userOrganizationEntityMapper
    ) {
        this.userOrganizationRepo = userOrganizationRepo;
        this.userOrganizationNotificationService = userOrganizationNotificationService;
        this.userOrganizationEntityMapper = userOrganizationEntityMapper;
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

    public Page<UserOrganization> getPendingJoinRequests(String organizationId, PageRequest pageRequest) {
        try {
            Page<UserOrganizationEntity> userOrganisationPage =
                    userOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatus(organizationId, MemberRequestStatus.PENDING, pageRequest);
            return userOrganisationPage.map(userOrganizationEntityMapper::fromEntity);
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
        UserOrganization request = userOrganisationOptional.get();

        MemberRequestStatus status = accepted ? MemberRequestStatus.ACCEPTED : MemberRequestStatus.DECLINED;
        request = UserOrganization.builder().id(request.getId()).role(request.getRole()).status(status).organization(request.getOrganization()).user(request.getUser()).build();
        request = save(request);
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(request);
        return request;
    }

    public Optional<UserOrganization> getById(String id) {
        try {
            Optional<UserOrganizationEntity> optional = userOrganizationRepo.findById(id);
            if (optional.isEmpty()) {
                return Optional.empty();
            }
            UserOrganization userOrganization = userOrganizationEntityMapper.fromEntity(optional.get());
            return Optional.of(userOrganization);
        } catch (Exception e) {
            log.error("Fetching UserOrganization with id {} failed: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public UserOrganization save(UserOrganization userOrganization) {
        try {
            UserOrganizationEntity userOrganizationEntity = userOrganizationRepo.save(userOrganizationEntityMapper.toEntity(userOrganization));
            return userOrganizationEntityMapper.fromEntity(userOrganizationEntity);
        } catch (Exception e) {
            log.error("Saving UserOrganisation failed: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
