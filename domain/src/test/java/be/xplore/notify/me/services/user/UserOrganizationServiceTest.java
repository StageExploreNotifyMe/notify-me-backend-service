package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.persistence.UserOrganizationRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserOrganizationServiceTest {

    @Autowired
    private UserOrganizationService userOrganizationService;

    @MockBean
    private UserOrganizationRepo userOrganizationRepo;
    @MockBean //Not relevant for these unit tests
    private UserOrganizationNotificationService userOrganizationNotificationService;

    @Autowired
    private User user;
    @Autowired
    private Organization organization;
    @Autowired
    private UserOrganization userOrganization;

    private void mockSave() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockFindById() {
        given(userOrganizationRepo.findById(any())).will(i -> {
            String id = i.getArgument(0);
            if (id.equals(userOrganization.getId())) {
                return Optional.of(userOrganization);
            }
            return Optional.empty();
        });
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserByOrganizationAndStatus(any(), any(MemberRequestStatus.class), any())).will(i -> {
            List<UserOrganization> pending = new ArrayList<>();
            if (i.getArgument(1).equals(MemberRequestStatus.PENDING)) {
                pending.add(userOrganization);
            }
            return new PageImpl<>(pending);
        });
    }

    @Test
    void userJoinOrganization() {
        mockSave();
        UserOrganization uo = userOrganizationService.userJoinOrganization(user, organization);
        assertNotNull(uo);
        assertEquals(user.getId(), uo.getUser().getId());
        assertEquals(organization, uo.getOrganization());
        assertEquals(Role.MEMBER, uo.getRole());
        assertEquals(MemberRequestStatus.PENDING, uo.getStatus());
    }

    @Test
    void getPendingJoinRequests() {
        mockUserOrganisationByOrganization_IdAndStatus();
        Page<UserOrganization> requests = userOrganizationService.getPendingJoinRequests(organization.getId(), PageRequest.of(0, 20));
        Assertions.assertEquals(1, requests.getSize());
    }

    @Test
    void resolvePendingJoinRequestAccepted() {
        mockFindById();
        mockSave();
        UserOrganization userOrganization = userOrganizationService.resolvePendingJoinRequest(this.userOrganization.getId(), true);
        assertEquals(MemberRequestStatus.ACCEPTED, userOrganization.getStatus());
    }

    @Test
    void resolvePendingJoinRequestRejected() {
        mockFindById();
        mockSave();
        UserOrganization userOrganization = userOrganizationService.resolvePendingJoinRequest(this.userOrganization.getId(), false);
        assertEquals(MemberRequestStatus.DECLINED, userOrganization.getStatus());
    }

    @Test
    void resolvePendingJoinRequestNotFound() {
        given(userOrganizationRepo.findById(any())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userOrganizationService.resolvePendingJoinRequest(userOrganization.getId(), true));
    }

    @Test
    void getAllUsersByOrganizationId() {
        mockUserOrganisationByOrganization_IdAndStatus();
        Page<UserOrganization> requests = userOrganizationService.getAllUsersByOrganizationId(organization.getId(), PageRequest.of(0, 20));
        Assertions.assertEquals(0, requests.getSize());
    }

    @Test
    void changeOrganizationMemberRole() {
        mockSave();
        assertEquals(Role.MEMBER, userOrganization.getRole());
        UserOrganization updated = userOrganizationService.changeOrganizationMemberRole(userOrganization, Role.MEMBER);
        assertEquals(Role.MEMBER, updated.getRole());
        UserOrganization updated2 = userOrganizationService.changeOrganizationMemberRole(updated, Role.ORGANIZATION_LEADER);
        assertEquals(Role.ORGANIZATION_LEADER, updated2.getRole());
    }

    @Test
    void getById() {
        mockFindById();
        UserOrganization foundEvent = userOrganizationService.getById(userOrganization.getId());
        assertEquals(userOrganization.getId(), foundEvent.getId());
    }

    @Test
    void getByIdNotFound() {
        mockFindById();
        assertThrows(NotFoundException.class, () -> userOrganizationService.getById("qdsf"));
    }

    @Test
    void addOrganizationLeaderToOrganization() {
        mockSave();
        UserOrganization userOrganization = userOrganizationService.addOrganizationLeaderToOrganization(organization, user);
        assertEquals(user.getId(), userOrganization.getUser().getId());
        assertEquals(organization.getId(), userOrganization.getOrganization().getId());
        assertEquals(Role.ORGANIZATION_LEADER, userOrganization.getRole());
    }

    @Test
    void getAllUserOrganizationsByUserId() {
        mockGetUserOrganizationsByUserId();
        List<UserOrganization> allUserOrganizationsByUserId = userOrganizationService.getAllUserOrganizationsByUserId(user.getId());
        assertNotNull(allUserOrganizationsByUserId);
    }

    private void mockGetUserOrganizationsByUserId() {
        given(userOrganizationRepo.getAllUserOrganizationsByUserId(any())).will(i -> {
            List<UserOrganization> uos = new ArrayList<>();
            uos.add(userOrganization);
            return uos;
        });
    }
}