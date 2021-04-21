package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.MemberRequestStatus;
import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Role;
import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.domain.UserOrganisation;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.repositories.UserOrganisationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserOrganizationService {

    private final UserOrganisationRepo userOrganisationRepo;

    public UserOrganizationService(UserOrganisationRepo userOrganisationRepo) {
        this.userOrganisationRepo = userOrganisationRepo;
    }

    public UserOrganisation userJoinOrganization(User user, Organization organization) {
        UserOrganisation userOrganisation = new UserOrganisation(null, user, organization, Role.MEMBER, MemberRequestStatus.PENDING);
        return save(userOrganisation);
    }

    public UserOrganisation save(UserOrganisation userOrganisation) {
        try {
            return userOrganisationRepo.save(userOrganisation);
        } catch (Exception e) {
            log.error("Saving UserOrganisation failed: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
