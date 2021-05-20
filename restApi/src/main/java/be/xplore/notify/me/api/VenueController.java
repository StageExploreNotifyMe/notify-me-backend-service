package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.venue.CreateVenueDto;
import be.xplore.notify.me.dto.venue.VenueDto;
import be.xplore.notify.me.mappers.VenueDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static be.xplore.notify.me.util.Converters.getPageNumber;

@RestController
@RequestMapping(value = "/admin/venue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class VenueController {
    private final VenueService venueService;
    private final UserService userService;
    private final VenueDtoMapper venueDtoMapper;

    public VenueController(VenueService venueService, UserService userService, VenueDtoMapper venueDtoMapper) {
        this.venueService = venueService;
        this.userService = userService;
        this.venueDtoMapper = venueDtoMapper;
    }

    @GetMapping
    public ResponseEntity<Page<VenueDto>> getVenues(@RequestParam(required = false) Integer page) {
        Page<Venue> venues = venueService.getAllVenues(getPageNumber(page));
        return new ResponseEntity<>(venues.map(venueDtoMapper::toDto), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<VenueDto> createVenue(@RequestBody CreateVenueDto createVenueDto) {
        Venue venue = venueService.createVenue(createVenueDto.getName());
        venueService.addVenueManagerToVenue(venue, getUser(createVenueDto));
        return new ResponseEntity<>(venueDtoMapper.toDto(venue), HttpStatus.CREATED);
    }

    private User getUser(CreateVenueDto createVenueDto) {
        Optional<User> optionalUser = userService.getById(createVenueDto.getVenueManagerId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user with id: " + createVenueDto.getVenueManagerId() + "found");
        }
        return optionalUser.get();
    }
}
