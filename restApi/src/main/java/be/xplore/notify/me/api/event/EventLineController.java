package be.xplore.notify.me.api.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.exceptions.Unauthorized;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.event.StaffingReminderDto;
import be.xplore.notify.me.dto.line.LineAssignEventDto;
import be.xplore.notify.me.dto.line.LineAssignOrganizationDto;
import be.xplore.notify.me.dto.line.LineMemberDto;
import be.xplore.notify.me.mappers.event.EventLineDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.event.LineService;
import be.xplore.notify.me.services.user.UserService;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/line", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventLineController {

    private final LineService lineService;
    private final EventLineService eventLineService;
    private final EventLineDtoMapper eventLineDtoMapper;
    private final EventService eventService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final ApiUtils apiUtils;

    public EventLineController(
            LineService lineService,
            EventLineService eventLineService,
            EventLineDtoMapper eventLineDtoMapper,
            EventService eventService,
            OrganizationService organizationService,
            UserService userService,
            ApiUtils apiUtils
    ) {
        this.lineService = lineService;
        this.eventLineService = eventLineService;
        this.eventLineDtoMapper = eventLineDtoMapper;
        this.eventService = eventService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.apiUtils = apiUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventLineDto> getEventLine(@PathVariable String id) {
        return new ResponseEntity<>(eventLineDtoMapper.toDto(eventLineService.getById(id)), HttpStatus.OK);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<Page<EventLineDto>> getEventLines(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<EventLine> linesOfEvent = eventLineService.getAllLinesOfEvent(eventService.getById(id).getId(), ApiUtils.getPageNumber(page));
        return new ResponseEntity<>(linesOfEvent.map(eventLineDtoMapper::toDto), HttpStatus.OK);
    }

    @GetMapping("/organization/{id}")
    public ResponseEntity<Page<EventLineDto>> getEventLinesOfOrganization(@PathVariable String id, @RequestParam(required = false) Integer page) {
        Page<EventLine> linesOfEvent = eventLineService.getAllLinesOfOrganization(organizationService.getById(id).getId(), ApiUtils.getPageNumber(page));
        return new ResponseEntity<>(linesOfEvent.map(eventLineDtoMapper::toDto), HttpStatus.OK);
    }

    @PostMapping("/{id}/staffingreminder")
    public ResponseEntity<StaffingReminderDto> sendStaffingReminder(@PathVariable String id, @RequestBody StaffingReminderDto dto, Authentication authentication) {
        ApiUtils.requirePathVarAndBodyMatch(id, dto.getEventLineId());
        EventLine line = eventLineService.getById(dto.getEventLineId());
        isAllowedOnThisEventLineAndHasRole(authentication, line, new Role[]{Role.LINE_MANAGER, Role.VENUE_MANAGER});
        eventLineService.sendStaffingReminder(line, dto.getCustomText());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("event/add")
    public ResponseEntity<EventLineDto> assignLineToEvent(@RequestBody LineAssignEventDto dto) {
        EventLine eventLine = eventLineService.addLineToEvent(
                lineService.getById(dto.getLineId()),
                eventService.getById(dto.getEventId()),
                userService.getById(dto.getLineManagerId())
        );
        return new ResponseEntity<>(eventLineDtoMapper.toDto(eventLine), HttpStatus.CREATED);
    }

    @PostMapping("{lineId}/assign/member")
    public ResponseEntity<EventLineDto> assignMemberToEventLine(@PathVariable String lineId, @RequestBody LineMemberDto dto, Authentication authentication) {
        ApiUtils.requirePathVarAndBodyMatch(lineId, dto.getEventLineId());

        User user = userService.getById(dto.getMemberId());
        EventLine line = eventLineService.getById(dto.getEventLineId());
        isAllowedOnThisEventLineAndHasRole(authentication, line, new Role[]{Role.ORGANIZATION_LEADER});

        EventLine updatedLine = eventLineService.assignUserToEventLine(user, line);
        return new ResponseEntity<>(eventLineDtoMapper.toDto(updatedLine), HttpStatus.OK);
    }

    @PostMapping("{lineId}/assign/organization")
    public ResponseEntity<EventLineDto> assignOrganizationToEventLine(@PathVariable String lineId, @RequestBody LineAssignOrganizationDto dto, Authentication authentication) {
        ApiUtils.requirePathVarAndBodyMatch(lineId, dto.getEventLineId());

        Organization organization = organizationService.getById(dto.getOrganizationId());
        EventLine line = eventLineService.getById(dto.getEventLineId());
        isAllowedOnThisEventLineAndHasRole(authentication, line, new Role[]{Role.LINE_MANAGER, Role.VENUE_MANAGER});

        EventLine updatedLine = eventLineService.assignOrganizationToLine(organization, line);
        return new ResponseEntity<>(eventLineDtoMapper.toDto(updatedLine), HttpStatus.OK);
    }

    @PostMapping("{id}/cancel")
    public ResponseEntity<EventLineDto> cancelEventLine(@PathVariable String id, Authentication authentication) {
        EventLine line = eventLineService.getById(id);
        isAllowedOnThisEventLineAndHasRole(authentication, line, new Role[]{Role.LINE_MANAGER, Role.VENUE_MANAGER});
        EventLine canceledLine = eventLineService.cancelEventLine(line);
        return new ResponseEntity<>(eventLineDtoMapper.toDto(canceledLine), HttpStatus.OK);
    }

    @PostMapping("{lineId}/cancel/member")
    public ResponseEntity<EventLineDto> cancelMemberEventLine(@PathVariable String lineId, @RequestBody LineMemberDto dto, Authentication authentication) {
        ApiUtils.requirePathVarAndBodyMatch(lineId, dto.getEventLineId());
        EventLine line = eventLineService.getById(dto.getEventLineId());
        User caller = isAllowedOnThisEventLineAndHasRole(authentication, line, new Role[]{Role.MEMBER});
        if (!ApiUtils.isAdmin(authentication) && !dto.getMemberId().equals(caller.getId())) {
            throw new Unauthorized("You are not allowed to cancel someone else's attendance");
        }

        EventLine updatedLine = eventLineService.cancelUserEventLine(dto.getMemberId(), line);
        return new ResponseEntity<>(eventLineDtoMapper.toDto(updatedLine), HttpStatus.OK);
    }

    private User isAllowedOnThisEventLineAndHasRole(Authentication authentication, EventLine eventLine, Role[] roles) {
        if (ApiUtils.isAdmin(authentication)) {
            return null;
        }
        ApiUtils.requireRole(authentication, roles);
        User user = apiUtils.requireUserFromAuthentication(authentication);
        if (getEventLineUsers(eventLine).stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new Unauthorized("You are not allowed to do this.");
        }
        return user;
    }

    private List<User> getEventLineUsers(EventLine eventLine) {
        List<User> allowedUsers = new ArrayList<>();
        allowedUsers.addAll(eventLine.getAssignedUsers());
        allowedUsers.addAll(eventLine.getEvent().getVenue().getLineManagers());
        allowedUsers.addAll(eventLine.getEvent().getVenue().getVenueManagers());
        return allowedUsers;
    }
}
