package be.xplore.notify.me.adapters;

import be.xplore.notify.me.domain.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class OrganizationAdapterTest {

    @Autowired
    private OrganizationAdapter organizationAdapter;

    @Test
    void findAll() {
        Page<Organization> page = organizationAdapter.findAll(PageRequest.of(0, 500));
        assertFalse(page.isEmpty());
    }

    @Test
    void save() {
        Organization organization = Organization.builder().name("test").build();
        Organization save = organizationAdapter.save(organization);
        assertEquals(organization.getName(), save.getName());
    }

    @Test
    void findById() {
        Optional<Organization> organization = organizationAdapter.findById("1");
        assertTrue(organization.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<Organization> organization = organizationAdapter.findById("rkqdsf");
        assertTrue(organization.isEmpty());
    }
}