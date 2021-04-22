package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import org.junit.jupiter.api.BeforeEach;
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

    private User user;
    private Organization organization;
    private UserOrganization request;

    @BeforeEach
    void setUp() {
        user = new User();
        organization = new Organization("1", "Example Organization");
        request = new UserOrganization("1", user, organization, Role.MEMBER, MemberRequestStatus.PENDING);
    }

    private void mockSave() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockFindById() {
        given(userOrganizationRepo.findById(any())).will(i -> {
            String id = i.getArgument(0);
            if (id.equals(request.getId())) {
                return Optional.of(request);
            }
            return Optional.empty();
        });
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserOrganisationByOrganization_IdAndStatus(any(), any(), any())).will(i -> {
            List<UserOrganization> pending = new ArrayList<>();
            pending.add(request);
            return new PageImpl<>(pending);
        });
    }

    @Test
    void userJoinOrganization() {
        mockSave();
        UserOrganization uo = userOrganizationService.userJoinOrganization(user, organization);
        assertNotNull(uo);
        assertEquals(user, uo.getUser());
        assertEquals(organization, uo.getOrganization());
        assertEquals(Role.MEMBER, uo.getRole());
        assertEquals(MemberRequestStatus.PENDING, uo.getStatus());
    }

    @Test
    void userJoinOrganizationDbException() {
        given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception("test exception")));
        assertThrows(DatabaseException.class, () -> userOrganizationService.userJoinOrganization(user, organization));
    }

    @Test
    void getPendingJoinRequests() {
        mockUserOrganisationByOrganization_IdAndStatus();
        Page<UserOrganization> requests = userOrganizationService.getPendingJoinRequests(organization.getId(), PageRequest.of(0, 20));
        assertEquals(1, requests.getSize());
    }

    @Test
    void getPendingJoinRequestsThrowsDbException() {
        given(userOrganizationRepo.getUserOrganisationByOrganization_IdAndStatus(any(), any(), any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> userOrganizationService.getPendingJoinRequests(organization.getId(), PageRequest.of(0, 20)));
    }

    @Test
    void resolvePendingJoinRequestAccepted() {
        mockFindById();
        mockSave();
        UserOrganization userOrganization = userOrganizationService.resolvePendingJoinRequest(request.getId(), true);
        assertEquals(MemberRequestStatus.ACCEPTED, userOrganization.getStatus());
    }

    @Test
    void resolvePendingJoinRequestRejected() {
        mockFindById();
        mockSave();
        UserOrganization userOrganization = userOrganizationService.resolvePendingJoinRequest(request.getId(), false);
        assertEquals(MemberRequestStatus.DECLINED, userOrganization.getStatus());
    }

    @Test
    void resolvePendingJoinRequestDbException() {
        given(userOrganizationRepo.findById(any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> userOrganizationService.resolvePendingJoinRequest(request.getId(), true));
    }

    @Test
    void resolvePendingJoinRequestNotFound() {
        given(userOrganizationRepo.findById(any())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userOrganizationService.resolvePendingJoinRequest(request.getId(), true));
    }
}