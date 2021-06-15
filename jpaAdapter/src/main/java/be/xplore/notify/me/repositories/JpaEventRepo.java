package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.event.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaEventRepo extends JpaRepository<EventEntity, Long> {
    Page<EventEntity> getAllByVenue_IdOrderByDateDesc(long venue_id, Pageable pageable);

    Page<EventEntity> getAllByDateBetweenOrderByDateDesc(LocalDateTime date, LocalDateTime date2, Pageable pageable);

    @Query("select o from EventEntity o where o.id in :ids")
    List<EventEntity> findAllByIds(@Param("ids") List<Long> ids);
}
