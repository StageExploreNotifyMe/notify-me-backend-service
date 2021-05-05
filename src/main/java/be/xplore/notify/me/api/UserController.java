package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.mappers.event.EventLineDtoMapper;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.user.UserService;
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
@RequestMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;
    private final EventLineDtoMapper eventLineDtoMapper;
    private final EventLineService eventLineService;

    public UserController(UserService userService, EventLineDtoMapper eventLineDtoMapper, EventLineService eventLineService) {
        this.userService = userService;
        this.eventLineDtoMapper = eventLineDtoMapper;
        this.eventLineService = eventLineService;
    }

    @GetMapping("/{id}/lines")
    public ResponseEntity<Page<EventLineDto>> getUserLines(@PathVariable String id, @RequestParam(required = false) Integer page) {
        int pageNumber = 0;
        if (page != null) {
            pageNumber = page;
        }
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found with id " + id);
        }

        Page<EventLine> allLinesOfUser = eventLineService.getAllLinesOfUser(userOptional.get(), pageNumber);
        return new ResponseEntity<>(allLinesOfUser.map(eventLineDtoMapper::toDto), HttpStatus.OK);

    }

}
