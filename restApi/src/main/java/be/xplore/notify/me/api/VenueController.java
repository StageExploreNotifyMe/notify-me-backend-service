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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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
    public ResponseEntity<Venue> createVenue(@RequestBody CreateVenueDto createVenueDto) {
        Venue venue = venueService.createVenue(createVenueDto.getName());
        venueService.addVenueManagerToVenue(venue, getUser(createVenueDto));
        return new ResponseEntity<>(venue, HttpStatus.CREATED);
    }

    @PatchMapping("/edit")
    public ResponseEntity<VenueDto> updateVenue(@RequestBody VenueDto venueDto) {
        Venue venue = venueService.updateVenue(venueDtoMapper.fromDto(venueDto));
        return new ResponseEntity<>(venueDtoMapper.toDto(venue), HttpStatus.OK);
    }

    private List<User> getUser(CreateVenueDto createVenueDto) {
        List<User> users = new ArrayList<>();
        for (String id : createVenueDto.getVenueManagerIds()) {
            Optional<User> user = userService.getById(id);
            if (user.isEmpty()) {
                throw new NotFoundException("No user with id: " + id + "found");
            }
            users.add(user.get());
        }
        return users;
    }
}
