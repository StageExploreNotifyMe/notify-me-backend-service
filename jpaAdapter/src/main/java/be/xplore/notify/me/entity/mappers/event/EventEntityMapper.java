package be.xplore.notify.me.entity.mappers.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.entity.mappers.VenueEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class EventEntityMapper implements EntityMapper<EventEntity, Event> {

    private final VenueEntityMapper venueEntityMapper;

    public EventEntityMapper(VenueEntityMapper venueEntityMapper) {
        this.venueEntityMapper = venueEntityMapper;
    }

    @Override
    public Event fromEntity(EventEntity eventEntity) {
        return Event.builder().id(eventEntity.getId())
                .date(eventEntity.getDate())
                .eventStatus(eventEntity.getEventStatus())
                .venue(venueEntityMapper.fromEntity(eventEntity.getVenue()))
                .name(eventEntity.getName())
                .build();
    }

    @Override
    public EventEntity toEntity(Event event) {
        return new EventEntity(event.getId(), event.getName(), event.getDate(), event.getEventStatus(), venueEntityMapper.toEntity(event.getVenue()));
    }
}
