package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VenueService extends RepoService<Venue, VenueEntity> {

    public VenueService(JpaRepository<VenueEntity, String> repo, EntityMapper<VenueEntity, Venue> entityMapper) {
        super(repo, entityMapper);
    }
}
