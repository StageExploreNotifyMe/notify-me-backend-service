package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.event.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface JpaEventRepo extends JpaRepository<EventEntity, Long> {
    Page<EventEntity> getAllByVenue_IdOrderByDate(long venue_id, Pageable pageable);

    Page<EventEntity> getAllByDateBetweenOrderByDate(LocalDateTime date, LocalDateTime date2, Pageable pageable);
}
