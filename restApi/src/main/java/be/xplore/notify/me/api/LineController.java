package be.xplore.notify.me.api;

import be.xplore.notify.me.api.util.Converters;
import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.dto.event.LineDto;
import be.xplore.notify.me.dto.mappers.event.LineDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.LineService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/line", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {

    private final LineService lineService;
    private final LineDtoMapper lineDtoMapper;
    private final VenueService venueService;
    private final Converters converters;

    public LineController(LineService lineService, LineDtoMapper lineDtoMapper, VenueService venueService, Converters converters) {
        this.lineService = lineService;
        this.lineDtoMapper = lineDtoMapper;
        this.venueService = venueService;
        this.converters = converters;
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<Page<LineDto>> getLinesOfVenue(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<Line> linePage = lineService.getAllByVenue(getVenueById(id).getId(), converters.getPageNumber(page));
        return new ResponseEntity<>(linePage.map(lineDtoMapper::toDto), HttpStatus.OK);
    }

    private Venue getVenueById(String id) {
        Optional<Venue> venueOptional = venueService.getById(id);
        if (venueOptional.isEmpty()) {
            throw new NotFoundException("Could not find venue with id " + id);
        }
        return venueOptional.get();
    }
}
