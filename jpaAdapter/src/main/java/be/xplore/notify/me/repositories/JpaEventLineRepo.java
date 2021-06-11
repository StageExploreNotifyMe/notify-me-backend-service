package be.xplore.notify.me.repositories;

import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaEventLineRepo extends JpaRepository<EventLineEntity, Long> {
    Page<EventLineEntity> getAllByEvent_IdOrderByLine(long event_id, Pageable pageable);

    Page<EventLineEntity> getAllByOrganization_IdOrderByEvent_date(long organization_id, Pageable pageable);

    Page<EventLineEntity> getAllByAssignedUsersContainsOrderByEvent_date(UserEntity assignedUsers, Pageable pageable);

    List<EventLineEntity> getAllByEvent(EventEntity event);

    List<EventLineEntity> getAllByLineManager_IdAndEventLineStatusNotAndEvent_EventStatusNotOrderByEvent_date(
            long lineManager_id, EventLineStatus eventLineStatus, EventStatus event_eventStatus
    );
}
