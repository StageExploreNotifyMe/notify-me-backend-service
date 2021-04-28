package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepo extends JpaRepository<VenueEntity, String> {
}
