package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.mappers.event.EventEntityMapper;
import be.xplore.notify.me.persistence.EventRepo;
import be.xplore.notify.me.repositories.JpaEventRepo;
import be.xplore.notify.me.util.LongParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Page<EventEntity> eventEntityPage = jpaEventRepo.getAllByVenue_IdOrderByDateDesc(LongParser.parseLong(venueId), pageRequest);
        return eventEntityPage.map(eventEntityMapper::fromEntity);
    }

    @Override
    public Optional<Event> findById(String id) {
        Optional<EventEntity> optional = jpaEventRepo.findById(LongParser.parseLong(id));
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

    @Transactional
    @Override
    public Page<Event> getAllEventsBetween(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, PageRequest pageRequest) {
        Page<EventEntity> eventEntitiesPage = jpaEventRepo.getAllByDateBetweenOrderByDateDesc(dateTimeStart, dateTimeEnd, pageRequest);
        return eventEntitiesPage.map(eventEntityMapper::fromEntity);
    }

    @Override
    public List<Event> findAllByIds(List<String> ids) {
        List<Long> longIds = ids.stream().map(LongParser::parseLong).collect(Collectors.toList());
        List<EventEntity> eventEntities = jpaEventRepo.findAllByIds(longIds);
        return eventEntities.stream().map(eventEntityMapper::fromEntity).collect(Collectors.toList());
    }

}
