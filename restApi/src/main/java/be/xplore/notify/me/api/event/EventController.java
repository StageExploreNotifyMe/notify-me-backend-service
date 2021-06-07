package be.xplore.notify.me.api.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.exceptions.Unauthorized;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventCreationDto;
import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.mappers.event.EventDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.util.ApiUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/event", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final EventService eventService;
    private final VenueService venueService;
    private final EventDtoMapper eventDtoMapper;
    private final ApiUtils apiUtils;

    public EventController(EventService eventService, VenueService venueService, EventDtoMapper eventDtoMapper, ApiUtils apiUtils) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.eventDtoMapper = eventDtoMapper;
        this.apiUtils = apiUtils;
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventCreationDto eventCreationDto, Authentication authentication) {
        Venue venue = venueService.getById(eventCreationDto.getVenueId());
        requireVenueManager(authentication, venue);

        Event event = eventService.createEvent(eventCreationDto.getEventDateTime(), eventCreationDto.getName(), venue);
        return new ResponseEntity<>(eventDtoMapper.toDto(event), HttpStatus.CREATED);
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<Page<EventDto>> getEventsOfVenue(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<Event> eventPage = eventService.getEventsOfVenue(id, ApiUtils.getPageNumber(page));
        Page<EventDto> eventDtoPage = eventPage.map(eventDtoMapper::toDto);
        return new ResponseEntity<>(eventDtoPage, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable String id) {
        return new ResponseEntity<>(eventDtoMapper.toDto(eventService.getById(id)), HttpStatus.OK);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventDto> cancelEvent(@PathVariable String id, Authentication authentication) {
        Event event = eventService.getById(id);
        requireVenueManager(authentication, event.getVenue());
        Event cancelEvent = eventService.cancelEvent(event);
        return new ResponseEntity<>(eventDtoMapper.toDto(cancelEvent), HttpStatus.OK);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<EventDto> publishEvent(@PathVariable String id, Authentication authentication) {
        Event event = eventService.getById(id);
        requireVenueManager(authentication, event.getVenue());
        Event publishedEvent = eventService.publishEvent(event);
        return new ResponseEntity<>(eventDtoMapper.toDto(publishedEvent), HttpStatus.OK);
    }

    @PostMapping("/{id}/private")
    public ResponseEntity<EventDto> makeEventPrivate(@PathVariable String id, Authentication authentication) {
        Event event = eventService.getById(id);
        requireVenueManager(authentication, event.getVenue());
        Event privateEvent = eventService.makeEventPrivate(event);
        return new ResponseEntity<>(eventDtoMapper.toDto(privateEvent), HttpStatus.OK);
    }

    private void requireVenueManager(Authentication authentication, Venue venue) {
        User caller = apiUtils.requireUserFromAuthentication(authentication);
        if (caller.getRoles().contains(Role.ADMIN)) {
            return;
        }

        if (venue.getVenueManagers().stream().noneMatch(user -> user.getId().equals(caller.getId()))) {
            throw new Unauthorized("You are not authorized to create an event");
        }
    }
}
