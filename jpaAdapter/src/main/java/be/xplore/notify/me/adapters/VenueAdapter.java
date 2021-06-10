package be.xplore.notify.me.adapters;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.mappers.VenueEntityMapper;
import be.xplore.notify.me.mappers.user.UserEntityMapper;
import be.xplore.notify.me.persistence.VenueRepo;
import be.xplore.notify.me.repositories.JpaVenueRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional
public class VenueAdapter implements VenueRepo {

    private final JpaVenueRepo repo;
    private final VenueEntityMapper entityMapper;
    private final UserEntityMapper userEntityMapper;

    public VenueAdapter(JpaVenueRepo repo, VenueEntityMapper entityMapper, UserEntityMapper userEntityMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
        this.userEntityMapper = userEntityMapper;
    }

    public Optional<Venue> findById(String id) {
        Optional<VenueEntity> optional = repo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Venue venue = entityMapper.fromEntity(optional.get());
        return Optional.of(venue);
    }

    @Override
    public Page<Venue> getAllVenues(PageRequest pageRequest) {
        Page<VenueEntity> entityPage = repo.findAll(pageRequest);
        return entityPage.map(entityMapper::fromEntity);
    }

    @Override
    public Optional<Venue> findVenueEntityByName(String name) {
        Optional<VenueEntity> optional = repo.findVenueEntityByName(name);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Venue venue = entityMapper.fromEntity(optional.get());
        return Optional.of(venue);
    }

    @Override
    public Page<Venue> getAllVenuesOfUser(User user, PageRequest pageRequest) {
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(userEntityMapper.toEntity(user));
        Page<VenueEntity> venuesOfUser = repo.findAllByLineManagersInOrVenueManagersIn(userEntityList, userEntityList, pageRequest);
        return venuesOfUser.map(entityMapper::fromEntity);
    }

    public Venue save(Venue venue) {
        VenueEntity venueEntity = repo.save(entityMapper.toEntity(venue));
        return entityMapper.fromEntity(venueEntity);
    }
}
