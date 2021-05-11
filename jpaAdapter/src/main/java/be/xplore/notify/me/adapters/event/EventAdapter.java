package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.mappers.event.EventEntityMapper;
import be.xplore.notify.me.persistence.EventRepo;
import be.xplore.notify.me.repositories.JpaEventRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class EventAdapter implements EventRepo {

    private final JpaEventRepo jpaEventRepo;
    private final EventEntityMapper eventEntityMapper;

    public EventAdapter(JpaEventRepo jpaEventRepo, EventEntityMapper eventEntityMapper) {
        this.jpaEventRepo = jpaEventRepo;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override
    public Page<Event> getEventsOfVenue(String venueId, PageRequest pageRequest) {
        Page<EventEntity> eventEntityPage = jpaEventRepo.getAllByVenue_IdOrderByDate(venueId, pageRequest);
        return eventEntityPage.map(eventEntityMapper::fromEntity);
    }

    @Override
    public Optional<Event> findById(String id) {
        Optional<EventEntity> optional = jpaEventRepo.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Event event = eventEntityMapper.fromEntity(optional.get());
        return Optional.of(event);
    }

    @Override
    public Event save(Event event) {
        EventEntity eventEntity = jpaEventRepo.save(eventEntityMapper.toEntity(event));
        return eventEntityMapper.fromEntity(eventEntity);
    }

}
