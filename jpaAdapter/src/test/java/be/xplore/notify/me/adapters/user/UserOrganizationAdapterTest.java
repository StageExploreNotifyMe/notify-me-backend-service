package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.UserOrganization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class UserOrganizationAdapterTest {

    @Autowired
    private UserOrganizationAdapter userOrganizationAdapter;

    @Test
    void getUserByOrganizationAndStatus() {
        Page<UserOrganization> userPage = userOrganizationAdapter.getUserByOrganizationAndStatus("1", MemberRequestStatus.ACCEPTED, PageRequest.of(0, 20));
        assertTrue(userPage.hasContent());
    }

    @Test
    void getAllOrganizationLeadersByOrganizationId() {
        List<UserOrganization> leaders = userOrganizationAdapter.getAllOrganizationLeadersByOrganizationId("1");
        assertNotNull(leaders);
    }

    @Test
    void save() {
        UserOrganization uo = UserOrganization.builder().role(Role.ORGANIZATION_LEADER).build();
        UserOrganization save = userOrganizationAdapter.save(uo);
        assertEquals(uo.getRole(), save.getRole());
    }

    @Test
    void findById() {
        Optional<UserOrganization> byId = userOrganizationAdapter.findById("1");
        assertTrue(byId.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<UserOrganization> byId = userOrganizationAdapter.findById("qmsdfj");
        assertTrue(byId.isEmpty());
    }

    @Test
    void getAllOrganizationLeadersByUserId() {
        List<UserOrganization> userOrganizationList = userOrganizationAdapter.getAllUserOrganizationsByUserId("1");
        assertEquals(1, userOrganizationList.size());
    }
}