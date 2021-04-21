package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.MemberRequestStatus;
import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Role;
import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.domain.UserOrganisation;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.repositories.UserOrganisationRepo;
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
    private UserOrganisationRepo userOrganisationRepo;

    private User user;
    private Organization organization;

    @BeforeEach
    void setUp() {
        user = new User();
        organization = new Organization();

        given(userOrganisationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void userJoinOrganization() {
        UserOrganisation uo = userOrganizationService.userJoinOrganization(user, organization);
        assertNotNull(uo);
        assertEquals(user, uo.getUser());
        assertEquals(organization, uo.getOrganization());
        assertEquals(Role.MEMBER, uo.getRole());
        assertEquals(MemberRequestStatus.PENDING, uo.getStatus());
    }

    @Test
    void userJoinOrganizationDbException() {
        given(userOrganisationRepo.save(any())).willThrow(new DatabaseException(new Exception("test exception")));
        assertThrows(DatabaseException.class, () -> userOrganizationService.userJoinOrganization(user, organization));
    }
}
