package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.VenueEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaVenueRepo extends JpaRepository<VenueEntity, String> {

    Optional<VenueEntity> findVenueEntityByName(String name);

    Page<VenueEntity> findAllByLineManagersInOrVenueManagersIn(List<UserEntity> lineManagers, List<UserEntity> venueManagers, Pageable pageable);
}
