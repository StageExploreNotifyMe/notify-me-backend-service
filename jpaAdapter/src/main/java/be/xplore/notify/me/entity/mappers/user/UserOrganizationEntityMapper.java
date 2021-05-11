package be.xplore.notify.me.entity.mappers.user;

import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import org.springframework.stereotype.Component;

@Component
public class UserOrganizationEntityMapper implements EntityMapper<UserOrganizationEntity, UserOrganization> {

    private final UserEntityMapper userEntityMapper;
    private final OrganizationEntityMapper organizationEntityMapper;

    public UserOrganizationEntityMapper(UserEntityMapper userEntityMapper, OrganizationEntityMapper organizationEntityMapper) {
        this.userEntityMapper = userEntityMapper;
        this.organizationEntityMapper = organizationEntityMapper;
    }

    @Override
    public UserOrganization fromEntity(UserOrganizationEntity userOrganizationEntity) {
        return UserOrganization.builder()
            .id(userOrganizationEntity.getId())
            .user(userEntityMapper.fromEntity(userOrganizationEntity.getUserEntity()))
            .organization(organizationEntityMapper.fromEntity(userOrganizationEntity.getOrganizationEntity()))
            .role(userOrganizationEntity.getRole())
            .status(userOrganizationEntity.getStatus())
            .build();
    }

    @Override
    public UserOrganizationEntity toEntity(UserOrganization userOrganization) {
        return new UserOrganizationEntity(
                userOrganization.getId(),
                userEntityMapper.toEntity(userOrganization.getUser()),
                organizationEntityMapper.toEntity(userOrganization.getOrganization()),
                userOrganization.getRole(),
                userOrganization.getStatus()
        );
    }
}
