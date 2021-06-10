package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.persistence.UserOrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserOrganizationService {

    private final UserService userService;
    private final UserOrganizationRepo userOrganizationRepo;
    private final UserOrganizationNotificationService userOrganizationNotificationService;

    public UserOrganizationService(
            UserService userService,
            UserOrganizationRepo userOrganizationRepo,
            UserOrganizationNotificationService userOrganizationNotificationService
    ) {
        this.userService = userService;
        this.userOrganizationRepo = userOrganizationRepo;
        this.userOrganizationNotificationService = userOrganizationNotificationService;
    }

    public UserOrganization userJoinOrganization(User user, Organization organization) {
        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .role(Role.MEMBER)
                .status(MemberRequestStatus.PENDING)
                .build();
        return userOrganizationRepo.save(userOrganization);
    }

    public Page<UserOrganization> getAllUsersByOrganizationId(String organizationId, PageRequest pageRequest) {
        return getUserByOrganizationAndStatus(organizationId, pageRequest, MemberRequestStatus.ACCEPTED);
    }

    public Page<UserOrganization> getPendingJoinRequests(String organizationId, PageRequest pageRequest) {
        return getUserByOrganizationAndStatus(organizationId, pageRequest, MemberRequestStatus.PENDING);
    }

    private Page<UserOrganization> getUserByOrganizationAndStatus(String organizationId, PageRequest pageRequest, MemberRequestStatus status) {
        return userOrganizationRepo.getUserByOrganizationAndStatus(organizationId, status, pageRequest);
    }

    public List<UserOrganization> getAllOrganizationLeadersByOrganizationId(String organizationId) {
        return userOrganizationRepo.getAllOrganizationLeadersByOrganizationId(organizationId);
    }

    public UserOrganization resolvePendingJoinRequest(String requestId, boolean accepted) {
        UserOrganization request = getById(requestId);
        MemberRequestStatus status = accepted ? MemberRequestStatus.ACCEPTED : MemberRequestStatus.DECLINED;
        request = UserOrganization.builder().id(request.getId()).role(request.getRole()).status(status).organization(request.getOrganization()).user(request.getUser()).build();
        request = userOrganizationRepo.save(request);
        updateUserRoles(request.getUser());
        userOrganizationNotificationService.sendResolvedPendingRequestNotification(request);
        return request;
    }

    public UserOrganization changeOrganizationMemberRole(UserOrganization userOrganization, Role roleToChangeTo) {
        if (userOrganization.getRole().equals(roleToChangeTo)) {
            updateUserRoles(userOrganization.getUser());
            return userOrganization;
        }

        UserOrganization updated = UserOrganization.builder()
                .id(userOrganization.getId())
                .status(userOrganization.getStatus())
                .organization(userOrganization.getOrganization())
                .user(userOrganization.getUser())
                .role(roleToChangeTo)
                .build();
        updateUserRoles(updated.getUser());
        userOrganizationNotificationService.sendOrganizationRoleChangeNotification(updated);

        return userOrganizationRepo.save(updated);
    }

    private void updateUserRoles(User user) {
        List<UserOrganization> userOrganizations = getAllUserOrganizationsByUserId(user.getId());
        for (Role toCheckRole : Arrays.asList(Role.MEMBER, Role.ORGANIZATION_LEADER)) {
            updateUserRole(user, userOrganizations, toCheckRole);
        }
    }

    private void updateUserRole(User user, List<UserOrganization> userOrganizations, Role roleToCheck) {
        if (userOrganizations.stream().anyMatch(uo -> uo.getRole() == roleToCheck && uo.getStatus() == MemberRequestStatus.ACCEPTED)) {
            userService.addRole(user, roleToCheck);
        } else {
            userService.removeRole(user, roleToCheck);
        }
    }

    public Optional<UserOrganization> findById(String id) {
        return userOrganizationRepo.findById(id);
    }

    public UserOrganization getById(String id) {
        Optional<UserOrganization> byId = findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new NotFoundException("No userOrganization found with id " + id);
    }

    public List<UserOrganization> getAllUserOrganizationsByUserId(String userId) {
        return userOrganizationRepo.getAllUserOrganizationsByUserId(userId);
    }

    public UserOrganization addOrganizationLeaderToOrganization(Organization organization, User user) {
        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .status(MemberRequestStatus.ACCEPTED)
                .role(Role.ORGANIZATION_LEADER)
                .build();
        return userOrganizationRepo.save(userOrganization);
    }
}
