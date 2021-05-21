package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import be.xplore.notify.me.mappers.user.UserOrganizationEntityMapper;
import be.xplore.notify.me.persistence.UserOrganizationRepo;
import be.xplore.notify.me.repositories.JpaUserOrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserOrganizationAdapter implements UserOrganizationRepo {

    private final JpaUserOrganizationRepo jpaUserOrganizationRepo;
    private final UserOrganizationEntityMapper userOrganizationEntityMapper;

    public UserOrganizationAdapter(
            JpaUserOrganizationRepo jpaUserOrganizationRepo,
            UserOrganizationEntityMapper userOrganizationEntityMapper
    ) {
        this.jpaUserOrganizationRepo = jpaUserOrganizationRepo;
        this.userOrganizationEntityMapper = userOrganizationEntityMapper;
    }

    @Override
    public Page<UserOrganization> getUserByOrganizationAndStatus(String organizationId, MemberRequestStatus status, PageRequest pageRequest) {
        Page<UserOrganizationEntity> userOrganisationPage =
                jpaUserOrganizationRepo.getUserOrganisationByOrganizationEntity_IdAndStatusOrderByUserEntity(organizationId, status, pageRequest);
        return userOrganisationPage.map(userOrganizationEntityMapper::fromEntity);
    }

    @Override
    @Transactional
    public List<UserOrganization> getAllOrganizationLeadersByOrganizationId(String organizationId) {
        List<UserOrganizationEntity> userOrganizationEntities =
                jpaUserOrganizationRepo.getUserOrganizationEntityByOrganizationEntity_Id(organizationId);
        return userOrganizationEntities
            .stream()
            .map(userOrganizationEntityMapper::fromEntity)
            .filter(u -> u.getRole().equals(Role.ORGANIZATION_LEADER))
            .collect(Collectors.toList());
    }

    @Override
    public UserOrganization save(UserOrganization userOrganization) {
        UserOrganizationEntity userOrganizationEntity = jpaUserOrganizationRepo.save(userOrganizationEntityMapper.toEntity(userOrganization));
        return userOrganizationEntityMapper.fromEntity(userOrganizationEntity);
    }

    @Override
    public Optional<UserOrganization> findById(String userOrganizationId) {
        Optional<UserOrganizationEntity> optional = jpaUserOrganizationRepo.findById(userOrganizationId);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        UserOrganization userOrganization = userOrganizationEntityMapper.fromEntity(optional.get());
        return Optional.of(userOrganization);
    }

    @Override
    public List<UserOrganization> getAllUserOrganizationsByUserId(String userId) {
        List<UserOrganizationEntity> entities = jpaUserOrganizationRepo.getUserOrganizationEntitiesByUserEntity_Id(userId);
        return entities.stream().map(userOrganizationEntityMapper::fromEntity).collect(Collectors.toList());
    }
}
