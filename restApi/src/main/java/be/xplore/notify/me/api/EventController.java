package be.xplore.notify.me.api;

import be.xplore.notify.me.util.Converters;
import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.dto.event.EventCreationDto;
import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.mappers.event.EventDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/event", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final EventService eventService;
    private final VenueService venueService;
    private final EventDtoMapper eventDtoMapper;
    private final Converters converters;

    public EventController(EventService eventService, VenueService venueService, EventDtoMapper eventDtoMapper, Converters converters) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.eventDtoMapper = eventDtoMapper;
        this.converters = converters;
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventCreationDto eventCreationDto) {
        Optional<Venue> venueOptional = venueService.getById(eventCreationDto.getVenueId());
        if (venueOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Event event = eventService.createEvent(eventCreationDto.getEventDateTime(), eventCreationDto.getName(), venueOptional.get());

        return new ResponseEntity<>(eventDtoMapper.toDto(event), HttpStatus.CREATED);
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<Page<EventDto>> getEventsOfVenue(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<Event> eventPage = eventService.getEventsOfVenue(id, converters.getPageNumber(page));
        Page<EventDto> eventDtoPage = eventPage.map(eventDtoMapper::toDto);
        return new ResponseEntity<>(eventDtoPage, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable String id) {
        return new ResponseEntity<>(eventDtoMapper.toDto(findEventById(id)), HttpStatus.OK);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventDto> cancelEvent(@PathVariable String id) {
        Event event = eventService.cancelEvent(findEventById(id));
        return new ResponseEntity<>(eventDtoMapper.toDto(event), HttpStatus.OK);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<EventDto> publishEvent(@PathVariable String id) {
        Event event = eventService.publishEvent(findEventById(id));
        return new ResponseEntity<>(eventDtoMapper.toDto(event), HttpStatus.OK);
    }

    private Event findEventById(String id) {
        Optional<Event> eventOptional = eventService.getById(id);
        if (eventOptional.isEmpty()) {
            throw new NotFoundException("No event found with id " + id);
        }
        return eventOptional.get();
    }
}
