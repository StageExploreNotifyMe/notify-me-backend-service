package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.repositories.OrganizationRepo;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Autowired
    private OrganizationEntityMapper organizationEntityMapper;

    private void mockFetchById() {
        given(organizationRepo.findById(any())).will(i -> {
            String id = i.getArgument(0);
            if (organization.getId().equals(id)) {
                return Optional.of(organizationEntityMapper.toEntity(organization));
            } else {
                return Optional.empty();
            }
        });
    }

    private void mockFetchPage() {
        given(organizationRepo.findAll(any(PageRequest.class))).will(i -> {
            List<OrganizationEntity> entityList = new ArrayList<>();
            entityList.add(organizationEntityMapper.toEntity(organization));
            return new PageImpl<>(entityList);
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
    void getOrganizations() {
        mockFetchPage();
        Page<Organization> organizationsPage = organizationService.getOrganizations(0);
        assertEquals(organization.getId(), organizationsPage.getContent().get(0).getId());
    }

}