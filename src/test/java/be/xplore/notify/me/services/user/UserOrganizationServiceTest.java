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
    private UserOrganizationEntityMapper userOrganizationEntityMapper;

    @Autowired
    private User user;
    @Autowired
    private Organization organization;
    @Autowired
    private UserOrganization request;

    private void mockSave() {
        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockFindById() {
        given(userOrganizationRepo.findById(any())).will(i -> {
            String id = i.getArgument(0);
            if (id.equals(request.getId())) {
                return Optional.of(userOrganizationEntityMapper.toEntity(request));
            }
            return Optional.empty();
        });
    }

    private void mockUserOrganisationByOrganization_IdAndStatus() {
        given(userOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatus(any(), any(), any())).will(i -> {
            List<UserOrganizationEntity> pending = new ArrayList<>();
            pending.add(userOrganizationEntityMapper.toEntity(request));
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
        given(userOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatus(any(), any(), any())).willThrow(new DatabaseException(new Exception()));
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