package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.event.EventLineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLineRepo extends JpaRepository<EventLineEntity, String> {
    Page<EventLineEntity> getAllByEvent_IdOrderByLine(String event_id, Pageable pageable);

    Page<EventLineEntity> getAllByOrganization_IdOrderByEvent_date(String organization_id, Pageable pageable);

    List<EventLineEntity> getAllByEvent(EventEntity event);
}
