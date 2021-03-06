package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.event.LineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLineRepo extends JpaRepository<LineEntity, Long> {
    Page<LineEntity> getAllByVenueEntity_IdOrderByName(long venueEntity_id, Pageable pageable);
}
