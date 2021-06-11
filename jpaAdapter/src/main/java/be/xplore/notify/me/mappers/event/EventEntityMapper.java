package be.xplore.notify.me.mappers.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.entity.event.EventEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.mappers.VenueEntityMapper;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

@Component
public class EventEntityMapper implements EntityMapper<EventEntity, Event> {

    private final VenueEntityMapper venueEntityMapper;

    public EventEntityMapper(VenueEntityMapper venueEntityMapper) {
        this.venueEntityMapper = venueEntityMapper;
    }

    @Override
    public Event fromEntity(EventEntity eventEntity) {
        if (eventEntity == null) {
            return null;
        }
        return Event.builder().id(String.valueOf(eventEntity.getId()))
            .date(eventEntity.getDate())
            .eventStatus(eventEntity.getEventStatus())
            .venue(venueEntityMapper.fromEntity(eventEntity.getVenue()))
            .name(eventEntity.getName())
            .build();
    }

    @Override
    public EventEntity toEntity(Event event) {
        if (event == null) {
            return null;
        }
        return new EventEntity(LongParser.parseLong(event.getId()), event.getName(), event.getDate(), event.getEventStatus(), venueEntityMapper.toEntity(event.getVenue()));
    }
}
