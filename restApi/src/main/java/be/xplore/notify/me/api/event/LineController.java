package be.xplore.notify.me.api.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.Unauthorized;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.line.LineCreationDto;
import be.xplore.notify.me.dto.line.LineDto;
import be.xplore.notify.me.mappers.event.LineDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.LineService;
import be.xplore.notify.me.util.ApiUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping(value = "/line", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {

    private final LineService lineService;
    private final LineDtoMapper lineDtoMapper;
    private final VenueService venueService;
    private final ApiUtils apiUtils;

    public LineController(LineService lineService, LineDtoMapper lineDtoMapper, VenueService venueService, ApiUtils apiUtils) {
        this.lineService = lineService;
        this.lineDtoMapper = lineDtoMapper;
        this.venueService = venueService;
        this.apiUtils = apiUtils;
    }

    @PatchMapping("/edit")
    public ResponseEntity<LineDto> editLine(@RequestBody LineDto lineDto, Authentication authentication) {
        doLineValidation(lineDto.getName(), lineDto.getNumberOfRequiredPeople());

        Venue venue = venueService.getById(lineDto.getVenueDto().getId());
        doLineVenueMangerCheck(authentication, venue);

        Line updated = lineService.updateLine(lineDtoMapper.fromDto(lineDto));
        return new ResponseEntity<>(lineDtoMapper.toDto(updated), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<LineDto> createLine(@RequestBody LineCreationDto dto, Authentication authentication) {
        Venue venue = venueService.getById(dto.getVenueId());
        doLineVenueMangerCheck(authentication, venue);

        doLineValidation(dto.getName(), dto.getNumberOfRequiredPeople());
        Line line = Line.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .numberOfRequiredPeople(dto.getNumberOfRequiredPeople())
                .venue(venue)
                .build();

        Line created = lineService.createLine(line);
        return new ResponseEntity<>(lineDtoMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<Page<LineDto>> getLinesOfVenue(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<Line> linePage = lineService.getAllByVenue(venueService.getById(id).getId(), ApiUtils.getPageNumber(page));
        return new ResponseEntity<>(linePage.map(lineDtoMapper::toDto), HttpStatus.OK);
    }

    private void doLineVenueMangerCheck(Authentication authentication, Venue venue) {
        ApiUtils.requireRole(authentication, Arrays.asList(Role.LINE_MANAGER, Role.VENUE_MANAGER));
        User user = apiUtils.requireUserFromAuthentication(authentication);
        if (venue.getLineManagers().stream().noneMatch(u -> u.getId().equals(user.getId())) && venue.getVenueManagers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new Unauthorized("You are not authorized to do this");
        }
    }

    private void doLineValidation(String name, int numberOfPeople) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("A line must have a name");
        }
        if (numberOfPeople < 0) {
            throw new IllegalArgumentException("The number of required people for a line cannot be negative");
        }
    }
}
