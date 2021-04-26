package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.MemberRequestStatus;
import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Role;
import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.domain.UserOrganization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    private User user;
    private Organization organization;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
        organization = Organization.builder().build();

        given(userOrganizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void userJoinOrganization() {
        UserOrganization uo = userOrganizationService.userJoinOrganization(user, organization);
        assertNotNull(uo);
        assertEquals(user.getId(), uo.getUser().getId());
        assertEquals(organization.getId(), uo.getOrganization().getId());
        assertEquals(Role.MEMBER, uo.getRole());
        assertEquals(MemberRequestStatus.PENDING, uo.getStatus());
    }

    @Test
    void userJoinOrganizationDbException() {
        given(userOrganizationRepo.save(any())).willThrow(new DatabaseException(new Exception("test exception")));
        assertThrows(DatabaseException.class, () -> userOrganizationService.userJoinOrganization(user, organization));
    }
}
