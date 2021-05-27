package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.user.UserOrganizationDto;
import be.xplore.notify.me.dto.user.UserOrganizationIdsDto;
import be.xplore.notify.me.dto.user.UserOrganizationProcessDto;
import be.xplore.notify.me.dto.user.UserOrganizationsDto;
import be.xplore.notify.me.mappers.user.UserOrganizationDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/userorganization", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserOrganizationController {

    private final UserOrganizationService userOrganizationService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final UserOrganizationDtoMapper userOrganizationDtoMapper;
    private final Converters converters;

    public UserOrganizationController(
            UserOrganizationService userOrganizationService,
            UserService userService,
            OrganizationService organizationService,
            UserOrganizationDtoMapper userOrganizationDtoMapper,
            Converters converters
    ) {
        this.userOrganizationService = userOrganizationService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.userOrganizationDtoMapper = userOrganizationDtoMapper;
        this.converters = converters;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserOrganizationsDto> getAllUserOrganizationsOfUser(@PathVariable String userId) {
        List<UserOrganizationDto> userOrganizations = userOrganizationService.getAllUserOrganizationsByUserId(userId)
                .stream().map(userOrganizationDtoMapper::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(new UserOrganizationsDto(userOrganizations), HttpStatus.OK);
    }

    @PostMapping("/request/join")
    public ResponseEntity<UserOrganizationDto> userJoinOrganization(@RequestBody UserOrganizationIdsDto dto) {
        Optional<User> optionalUser = userService.getById(dto.getUserId());

        Optional<Organization> optionalOrganization = organizationService.getById(dto.getOrganizationId());
        if (optionalUser.isEmpty() || optionalOrganization.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserOrganization userOrganization = userOrganizationService.userJoinOrganization(optionalUser.get(), optionalOrganization.get());
        UserOrganizationDto returnDto = userOrganizationDtoMapper.toDto(userOrganization);
        return new ResponseEntity<>(returnDto, HttpStatus.CREATED);
    }

    @PostMapping("/request/process")
    public ResponseEntity<Boolean> processJoinRequest(@RequestBody UserOrganizationProcessDto dto) {
        userOrganizationService.resolvePendingJoinRequest(dto.getUserOrganizationId(), dto.isAccepted());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/requests/{organizationId}/pending")
    public ResponseEntity<Page<UserOrganizationDto>> getOpenUserOrganizationRequests(@PathVariable String organizationId, @RequestParam(required = false) Integer page) {
        int pageNumber = converters.getPageNumber(page);
        Page<UserOrganization> requests = userOrganizationService.getPendingJoinRequests(organizationId, PageRequest.of(pageNumber, 20));
        return getPageResponseEntity(requests);
    }

    @GetMapping("/{organizationId}/users")
    public ResponseEntity<Page<UserOrganizationDto>> getUsersOfOrganization(@PathVariable String organizationId, @RequestParam(required = false) Integer page) {
        int pageNumber = converters.getPageNumber(page);
        Optional<Organization> organizationOptional = organizationService.getById(organizationId);
        if (organizationOptional.isEmpty()) {
            throw new NotFoundException("No organization with id " + organizationId + " found");
        }

        Page<UserOrganization> requests = userOrganizationService.getAllUsersByOrganizationId(organizationOptional.get().getId(), PageRequest.of(pageNumber, 20));
        return getPageResponseEntity(requests);
    }

    @PostMapping("/{id}/promote")
    public ResponseEntity<UserOrganizationDto> promoteMember(@PathVariable String id) {
        UserOrganization userOrganization = getUserOrganizationById(id);
        UserOrganization promoted = userOrganizationService.changeOrganizationMemberRole(userOrganization, Role.ORGANIZATION_LEADER);
        return new ResponseEntity<>(userOrganizationDtoMapper.toDto(promoted), HttpStatus.OK);
    }

    @PostMapping("/{id}/demote")
    public ResponseEntity<UserOrganizationDto> demoteMember(@PathVariable String id) {
        UserOrganization userOrganization = getUserOrganizationById(id);
        UserOrganization promoted = userOrganizationService.changeOrganizationMemberRole(userOrganization, Role.MEMBER);
        return new ResponseEntity<>(userOrganizationDtoMapper.toDto(promoted), HttpStatus.OK);
    }

    private UserOrganization getUserOrganizationById(String id) {
        Optional<UserOrganization> userOrganizationOptional = userOrganizationService.getById(id);
        if (userOrganizationOptional.isEmpty()) {
            throw new NotFoundException("Could not find a userOrganization with id " + id);
        }
        return userOrganizationOptional.get();
    }

    private ResponseEntity<Page<UserOrganizationDto>> getPageResponseEntity(Page<UserOrganization> requests) {
        Page<UserOrganizationDto> userOrganizationDto = requests.map(userOrganizationDtoMapper::toDto);
        return new ResponseEntity<>(userOrganizationDto, HttpStatus.OK);
    }
}
