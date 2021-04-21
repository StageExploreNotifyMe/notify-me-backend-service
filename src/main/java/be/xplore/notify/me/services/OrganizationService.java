package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    public Organization getOrganizationById(String organizationId) {
        return new Organization(organizationId, "");
    }
}
