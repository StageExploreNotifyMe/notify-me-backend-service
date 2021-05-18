package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.event.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEventRepo extends JpaRepository<EventEntity, String> {
    Page<EventEntity> getAllByVenue_IdOrderByDate(String venue_id, Pageable pageable);
}
