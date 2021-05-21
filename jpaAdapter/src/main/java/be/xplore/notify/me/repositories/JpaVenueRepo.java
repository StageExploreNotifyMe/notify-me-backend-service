package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaVenueRepo extends JpaRepository<VenueEntity, String> {

    Optional<VenueEntity> findVenueEntityByName(String name);
}
