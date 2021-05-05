package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.event.LineAssignEventDto;
import be.xplore.notify.me.dto.event.LineAssignOrganizationDto;
import be.xplore.notify.me.dto.event.LineDto;
import be.xplore.notify.me.dto.mappers.event.EventLineDtoMapper;
import be.xplore.notify.me.dto.mappers.event.LineDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.event.LineService;
import be.xplore.notify.me.services.user.UserService;
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
@RequestMapping(value = "/line", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {

    private final LineService lineService;
    private final EventLineService eventLineService;
    private final LineDtoMapper lineDtoMapper;
    private final EventLineDtoMapper eventLineDtoMapper;
    private final EventService eventService;
    private final OrganizationService organizationService;
    private final VenueService venueService;
    private final UserService userService;

    public LineController(
            LineService lineService,
            EventLineService eventLineService,
            LineDtoMapper lineDtoMapper,
            EventLineDtoMapper eventLineDtoMapper,
            EventService eventService,
            OrganizationService organizationService,
            VenueService venueService,
            UserService userService) {
        this.lineService = lineService;
        this.eventLineService = eventLineService;
        this.lineDtoMapper = lineDtoMapper;
        this.eventLineDtoMapper = eventLineDtoMapper;
        this.eventService = eventService;
        this.organizationService = organizationService;
        this.venueService = venueService;
        this.userService = userService;
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<Page<LineDto>> getLinesOfVenue(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<Line> linePage = lineService.getAllByVenue(getVenueById(id).getId(), getPageNumber(page));
        return new ResponseEntity<>(linePage.map(lineDtoMapper::toDto), HttpStatus.OK);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<Page<EventLineDto>> getEventLines(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<EventLine> linesOfEvent = eventLineService.getAllLinesOfEvent(getEventById(id).getId(), getPageNumber(page));
        return new ResponseEntity<>(linesOfEvent.map(eventLineDtoMapper::toDto), HttpStatus.OK);
    }

    @PostMapping("event/add")
    public ResponseEntity<EventLineDto> assignLineToEvent(@RequestBody LineAssignEventDto dto) {
        EventLine eventLine = eventLineService.addLineToEvent(getLineById(dto.getLineId()), getEventById(dto.getEventId()), getUserById(dto.getLineManagerId()));
        return new ResponseEntity<>(eventLineDtoMapper.toDto(eventLine), HttpStatus.CREATED);
    }

    @PostMapping("{lineId}/assign/organization")
    public ResponseEntity<EventLineDto> assignOrganizationToEventLine(@PathVariable String lineId, @RequestBody LineAssignOrganizationDto dto) {
        if (!lineId.equals(dto.getEventLineId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Organization organization = getOrganizationById(dto.getOrganizationId());
        EventLine line = getEventLineById(dto.getEventLineId());

        EventLine updatedLine = eventLineService.assignOrganizationToLine(organization, line);
        return new ResponseEntity<>(eventLineDtoMapper.toDto(updatedLine), HttpStatus.OK);
    }

    private int getPageNumber(Integer page) {
        int pageNumber = 0;
        if (page != null) {
            pageNumber = page;
        }
        return pageNumber;
    }

    private Organization getOrganizationById(String id) {
        Optional<Organization> organizationOptional = organizationService.getById(id);
        if (organizationOptional.isEmpty()) {
            throw new NotFoundException("Could not find any organization with id " + id);
        }
        return organizationOptional.get();
    }

    private EventLine getEventLineById(String id) {
        Optional<EventLine> lineOptional = eventLineService.getById(id);
        if (lineOptional.isEmpty()) {
            throw new NotFoundException("Could not find any eventline with id " + id);
        }
        return lineOptional.get();
    }

    private Line getLineById(String id) {
        Optional<Line> lineOptional = lineService.getById(id);
        if (lineOptional.isEmpty()) {
            throw new NotFoundException("Could not find any line with id " + id);
        }
        return lineOptional.get();
    }

    private Venue getVenueById(String id) {
        Optional<Venue> venueOptional = venueService.getById(id);
        if (venueOptional.isEmpty()) {
            throw new NotFoundException("Could not find venue with id " + id);
        }
        return venueOptional.get();
    }

    private User getUserById(String id) {
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Could not find user with id " + id);
        }
        return userOptional.get();
    }

    private Event getEventById(String id) {
        Optional<Event> eventOptional = eventService.getById(id);
        if (eventOptional.isEmpty()) {
            throw new NotFoundException("Could not find an event with id " + id);
        }
        return eventOptional.get();
    }

}
