package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.venue.VenueDto;
import be.xplore.notify.me.mappers.VenueDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.ApiUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/venue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class VenueController {
    private final VenueService venueService;
    private final UserService userService;
    private final VenueDtoMapper venueDtoMapper;

    public VenueController(VenueService venueService, UserService userService, VenueDtoMapper venueDtoMapper) {
        this.venueService = venueService;
        this.userService = userService;
        this.venueDtoMapper = venueDtoMapper;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<VenueDto>> getVenues(@PathVariable String userId, @RequestParam(required = false) Integer page) {
        User user = userService.getById(userId);
        Page<Venue> venuePage;
        if (user.getRoles().contains(Role.ADMIN)) {
            venuePage = venueService.getAllVenues(ApiUtils.getPageNumber(page));
        } else {
            venuePage = venueService.getAllVenuesOfUser(user, ApiUtils.getPageNumber(page));
        }
        return new ResponseEntity<>(venuePage.map(venueDtoMapper::toDto), HttpStatus.OK);
    }

}
