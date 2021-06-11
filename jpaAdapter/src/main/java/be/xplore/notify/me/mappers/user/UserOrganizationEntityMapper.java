package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.util.LongParser;
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
            .id(String.valueOf(userOrganizationEntity.getId()))
            .user(userEntityMapper.fromEntity(userOrganizationEntity.getUserEntity()))
            .organization(organizationEntityMapper.fromEntity(userOrganizationEntity.getOrganizationEntity()))
            .role(userOrganizationEntity.getRole())
            .status(userOrganizationEntity.getStatus())
            .build();
    }

    @Override
    public UserOrganizationEntity toEntity(UserOrganization userOrganization) {
        return new UserOrganizationEntity(
                LongParser.parseLong(userOrganization.getId()),
                userEntityMapper.toEntity(userOrganization.getUser()),
                organizationEntityMapper.toEntity(userOrganization.getOrganization()),
                userOrganization.getRole(),
                userOrganization.getStatus()
        );
    }
}
