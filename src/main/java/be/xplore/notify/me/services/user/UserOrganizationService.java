package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserOrganizationService {

    private final UserOrganizationRepo userOrganizationRepo;
    private final UserOrganizationEntityMapper userOrganizationEntityMapper;
    private final UserOrganizationNotificationService userOrganizationNotificationService;

    public UserOrganizationService(
            UserOrganizationRepo userOrganizationRepo,
            UserOrganizationEntityMapper userOrganizationEntityMapper,
            UserOrganizationNotificationService userOrganizationNotificationService
    ) {
        this.userOrganizationRepo = userOrganizationRepo;
        this.userOrganizationEntityMapper = userOrganizationEntityMapper;
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
        Page<UserOrganizationEntity> userOrganisationPage =
                userOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatusOrderByUserEntity(organizationId, status, pageRequest);
        return userOrganisationPage.map(userOrganizationEntityMapper::fromEntity);
    }

    public List<UserOrganization> getAllOrganizationLeadersByOrganizationId(String organizationId) {
        List<UserOrganizationEntity> userOrganizationEntities =
                userOrganizationRepo.getUserOrganizationEntityByOrganizationEntity_Id(organizationId);
        return userOrganizationEntities
                .stream()
                .map(userOrganizationEntityMapper::fromEntity)
                .filter(u -> u.getRole().equals(Role.ORGANIZATION_LEADER))
                .collect(Collectors.toList());
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
        userOrganizationNotificationService.sendOrganizationRoleChangeNotification(updated);

        return save(updated);
    }

    public Optional<UserOrganization> getById(String id) {
        Optional<UserOrganizationEntity> optional = userOrganizationRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        UserOrganization userOrganization = userOrganizationEntityMapper.fromEntity(optional.get());
        return Optional.of(userOrganization);
    }

    public UserOrganization save(UserOrganization userOrganization) {
        UserOrganizationEntity userOrganizationEntity = userOrganizationRepo.save(userOrganizationEntityMapper.toEntity(userOrganization));
        return userOrganizationEntityMapper.fromEntity(userOrganizationEntity);
    }
}
