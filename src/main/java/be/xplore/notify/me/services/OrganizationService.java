package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.entity.mappers.OrganizationEntityMapper;
import be.xplore.notify.me.repositories.OrganizationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrganizationService extends RepoService<Organization, OrganizationEntity> {

    public OrganizationService(OrganizationRepo repo, OrganizationEntityMapper entityMapper) {
        super(repo, entityMapper);
    }

}
