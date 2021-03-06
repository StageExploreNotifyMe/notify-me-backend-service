package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.persistence.OrganizationRepo;
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
class OrganizationServiceTest {
    @Autowired
    private OrganizationService organizationService;

    @MockBean
    private OrganizationRepo organizationRepo;

    @Autowired
    private Organization organization;

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

    private void mockFetchPage() {
        given(organizationRepo.findAll(any(PageRequest.class))).will(i -> {
            List<Organization> organizations = new ArrayList<>();
            organizations.add(organization);
            return new PageImpl<>(organizations);
        });
    }

    private void mockGetOrgByName(boolean returnSomething) {
        given(organizationRepo.findByName(any())).will(i -> {
            if (returnSomething) {
                return Optional.of(organization);
            } else {
                return Optional.empty();
            }
        });
    }

    private void mockSave() {
        given(organizationRepo.save(any())).will(i -> i.getArgument(0));
    }

    @Test
    void getById() {
        mockFetchById();
        Organization foundEvent = organizationService.getById(organization.getId());
        assertEquals(organization.getId(), foundEvent.getId());
    }

    @Test
    void getByIdNotFound() {
        mockFetchById();
        assertThrows(NotFoundException.class, () -> organizationService.getById("qdsf"));
    }

    @Test
    void getOrganizations() {
        mockFetchPage();
        Page<Organization> organizationsPage = organizationService.getOrganizations(0);
        assertEquals(organization.getId(), organizationsPage.getContent().get(0).getId());
    }

    @Test
    void save() {
        mockSave();
        Organization saved = organizationService.save(organization);
        assertEquals(organization.getId(), saved.getId());
    }

    @Test
    void createOrganization() {
        mockGetOrgByName(false);
        mockSave();
        String name = "testCreateOrganization";
        Organization testCreateOrganization = organizationService.createOrganization(name);
        assertNotNull(testCreateOrganization);
        assertEquals(name, testCreateOrganization.getName());
    }

    @Test
    void createOrganizationAlreadyExists() {
        mockGetOrgByName(true);
        mockSave();
        assertThrows(AlreadyExistsException.class, () -> organizationService.createOrganization("qdsfqsdf"));
    }

    @Test
    void updateOrganization() {
        mockSave();
        mockFetchById();
        String newName = "updatedName";
        Organization updatedOrg = organizationService.updateOrganization(Organization.builder().id(this.organization.getId()).name(newName).build());
        assertEquals(organization.getId(), updatedOrg.getId());
        assertEquals(newName, updatedOrg.getName());
    }

    @Test
    void updateOrganizationNotFound() {
        given(organizationRepo.findById(any())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> organizationService.updateOrganization(Organization.builder().id(this.organization.getId()).name("qdsf").build()));
    }
}