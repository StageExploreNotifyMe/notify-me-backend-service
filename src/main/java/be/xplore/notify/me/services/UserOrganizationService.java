package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.MemberRequestStatus;
import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Role;
import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.domain.UserOrganization;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.UserOrganizationEntity;
import be.xplore.notify.me.entity.mappers.UserOrganizationEntityMapper;
import be.xplore.notify.me.repositories.UserOrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserOrganizationService {

    private final UserOrganizationRepo userOrganizationRepo;
    private final UserOrganizationEntityMapper userOrganizationEntityMapper;

    public UserOrganizationService(UserOrganizationRepo userOrganizationRepo, UserOrganizationEntityMapper userOrganizationEntityMapper) {
        this.userOrganizationRepo = userOrganizationRepo;
        this.userOrganizationEntityMapper = userOrganizationEntityMapper;
    }

    public UserOrganization userJoinOrganization(User user, Organization organization) {
        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .role(Role.MEMBER)
                .status(MemberRequestStatus.PENDING)
                .build();
        return save(userOrganization);
    }

    public UserOrganization save(UserOrganization userOrganization) {
        try {
            UserOrganizationEntity userOrganizationEntity = userOrganizationRepo.save(userOrganizationEntityMapper.toEntity(userOrganization));
            return userOrganizationEntityMapper.fromEntity(userOrganizationEntity);
        } catch (Exception e) {
            log.error("Saving UserOrganisation failed: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
