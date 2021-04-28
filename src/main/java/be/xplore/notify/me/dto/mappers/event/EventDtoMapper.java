package be.xplore.notify.me.dto.mappers.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.dto.mappers.DtoMapper;
import be.xplore.notify.me.dto.mappers.VenueDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper implements DtoMapper<EventDto, Event> {
    private final VenueDtoMapper venueDtoMapper;

    public EventDtoMapper(VenueDtoMapper venueDtoMapper) {
        this.venueDtoMapper = venueDtoMapper;
    }

    @Override
    public Event fromDto(EventDto d) {
        return Event.builder().id(d.getId()).name(d.getName()).date(d.getDate()).eventStatus(d.getEventStatus()).venue(venueDtoMapper.fromDto(d.getVenue())).build();
    }

    @Override
    public EventDto toDto(Event d) {
        return new EventDto(d.getId(), d.getName(), d.getDate(), d.getEventStatus(), venueDtoMapper.toDto(d.getVenue()));
    }
}
