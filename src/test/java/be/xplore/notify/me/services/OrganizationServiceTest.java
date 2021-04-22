package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.repositories.OrganizationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class OrganizationServiceTest {
    @Autowired
    private OrganizationService organizationService;

    @MockBean
    private OrganizationRepo organizationRepo;

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization("1", "Test");
    }

    private void mockFetchById() {
        given(organizationRepo.findById(any())).will(i -> {
            String id = i.getArgument(0);
            if (organization.getId().equals(id)) {
                return Optional.of(organization);
            } else {
                return Optional.empty();
            }
        });
    }

    @Test
    void getOrganizationById() {
        mockFetchById();
        Optional<Organization> byId = organizationService.getById(organization.getId());
        assertTrue(byId.isPresent());
        assertEquals(organization, byId.get());
    }

    @Test
    void getOrganizationByIdThrowsDbException() {
        given(organizationRepo.findById(any())).willThrow(new DatabaseException(new Exception()));
        assertThrows(DatabaseException.class, () -> organizationService.getById(organization.getId()));
    }
}