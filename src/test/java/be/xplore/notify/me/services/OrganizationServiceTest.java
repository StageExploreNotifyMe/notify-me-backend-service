package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrganizationServiceTest {
    @Autowired
    private OrganizationService organizationService;

    @Test
    void getOrganizationById() {
        String id = "test";
        Organization organization = organizationService.getOrganizationById(id);
        assertEquals(id, organization.getId());
    }
}