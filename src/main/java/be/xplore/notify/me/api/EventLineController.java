package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.event.LineAssignEventDto;
import be.xplore.notify.me.dto.event.LineAssignMemberDto;
import be.xplore.notify.me.dto.event.LineAssignOrganizationDto;
import be.xplore.notify.me.dto.mappers.event.EventLineDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
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
public class EventLineController {

    private final LineService lineService;
    private final EventLineService eventLineService;
    private final EventLineDtoMapper eventLineDtoMapper;
    private final EventService eventService;
    private final OrganizationService organizationService;
    private final UserService userService;

    public EventLineController(
            LineService lineService,
            EventLineService eventLineService,
            EventLineDtoMapper eventLineDtoMapper,
            EventService eventService,
            OrganizationService organizationService,
            UserService userService
    ) {
        this.lineService = lineService;
        this.eventLineService = eventLineService;
        this.eventLineDtoMapper = eventLineDtoMapper;
        this.eventService = eventService;
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventLineDto> getEventLine(@PathVariable String id) {
        return new ResponseEntity<>(eventLineDtoMapper.toDto(getEventLineById(id)), HttpStatus.OK);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<Page<EventLineDto>> getEventLines(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<EventLine> linesOfEvent = eventLineService.getAllLinesOfEvent(getEventById(id).getId(), getPageNumber(page));
        return new ResponseEntity<>(linesOfEvent.map(eventLineDtoMapper::toDto), HttpStatus.OK);
    }

    @GetMapping("/organization/{id}")
    public ResponseEntity<Page<EventLineDto>> getEventLinesOfOrganization(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<EventLine> linesOfEvent = eventLineService.getAllLinesOfOrganization(getOrganizationById(id).getId(), getPageNumber(page));
        return new ResponseEntity<>(linesOfEvent.map(eventLineDtoMapper::toDto), HttpStatus.OK);
    }

    @PostMapping("event/add")
    public ResponseEntity<EventLineDto> assignLineToEvent(@RequestBody LineAssignEventDto dto) {
        EventLine eventLine = eventLineService.addLineToEvent(getLineById(dto.getLineId()), getEventById(dto.getEventId()));
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

    @PostMapping("{lineId}/assign/member")
    public ResponseEntity<EventLineDto> assignMemberToEventLine(@PathVariable String lineId, @RequestBody LineAssignMemberDto dto) {
        if (!lineId.equals(dto.getEventLineId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = getUserById(dto);
        EventLine line = getEventLineById(dto.getEventLineId());

        EventLine updatedLine = eventLineService.assignUserToEventLine(user, line);
        return new ResponseEntity<>(eventLineDtoMapper.toDto(updatedLine), HttpStatus.OK);
    }

    private int getPageNumber(Integer page) {
        int pageNumber = 0;
        if (page != null) {
            pageNumber = page;
        }
        return pageNumber;
    }

    private User getUserById(LineAssignMemberDto dto) {
        Optional<User> userOptional = userService.getById(dto.getMemberId());
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Could not find a user with id " + dto.getMemberId());
        }
        return userOptional.get();
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

    private Event getEventById(String id) {
        Optional<Event> eventOptional = eventService.getById(id);
        if (eventOptional.isEmpty()) {
            throw new NotFoundException("Could not find an event with id " + id);
        }
        return eventOptional.get();
    }

}
