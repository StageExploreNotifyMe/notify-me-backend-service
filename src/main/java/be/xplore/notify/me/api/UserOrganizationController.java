package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.dto.mappers.UserOrganizationDtoMapper;
import be.xplore.notify.me.dto.user.UserOrganizationDto;
import be.xplore.notify.me.dto.user.UserOrganizationIdsDto;
import be.xplore.notify.me.dto.user.UserOrganizationProcessDto;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import be.xplore.notify.me.services.user.UserService;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/userorganization", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserOrganizationController {

    private final UserOrganizationService userOrganizationService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;
    private final UserOrganizationDtoMapper userOrganizationDtoMapper;

    public UserOrganizationController(
                UserOrganizationService userOrganizationService, UserService userService,
                OrganizationService organizationService, ModelMapper modelMapper,
                UserOrganizationDtoMapper userOrganizationDtoMapper
    ) {
        this.userOrganizationService = userOrganizationService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.modelMapper = modelMapper;
        this.userOrganizationDtoMapper = userOrganizationDtoMapper;
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

    @GetMapping("/requests/{organizationId}/pending/{page}")
    public ResponseEntity<Page<UserOrganizationDto>> getOpenUserOrganizationRequests(@PathVariable String organizationId, @PathVariable int page) {
        Page<UserOrganization> requests = userOrganizationService.getPendingJoinRequests(organizationId, PageRequest.of(page, 20));
        Page<UserOrganizationDto> userOrganizationDto = requests.map(userOrganization -> modelMapper.map(userOrganization, UserOrganizationDto.class));
        return new ResponseEntity<>(userOrganizationDto, HttpStatus.OK);

    }
}
