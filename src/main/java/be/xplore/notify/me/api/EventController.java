package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.dto.event.EventCreationDto;
import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.dto.mappers.event.EventDtoMapper;
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

    public EventController(EventService eventService, VenueService venueService, EventDtoMapper eventDtoMapper) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.eventDtoMapper = eventDtoMapper;
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
        int pageNumber = 0;
        if (page != null) {
            pageNumber = page;
        }
        Page<Event> eventPage = eventService.getEventsOfVenue(id, pageNumber);
        Page<EventDto> eventDtoPage = eventPage.map(eventDtoMapper::toDto);
        return new ResponseEntity<>(eventDtoPage, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable String id) {
        Optional<Event> eventOptional = eventService.getById(id);
        if (eventOptional.isEmpty()) {
            throw new NotFoundException("Could not find an event with id " + id);
        }
        return new ResponseEntity<>(eventDtoMapper.toDto(eventOptional.get()), HttpStatus.OK);
    }
}
