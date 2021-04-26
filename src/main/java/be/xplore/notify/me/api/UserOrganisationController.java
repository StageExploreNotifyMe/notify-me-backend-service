package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.User;
import be.xplore.notify.me.dto.UserOrganizationDto;
import be.xplore.notify.me.dto.UserOrganizationIdsDto;
import be.xplore.notify.me.dto.mappers.UserOrganizationDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.UserOrganizationService;
import be.xplore.notify.me.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/userorganisation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserOrganisationController {

    private final UserOrganizationService userOrganizationService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final UserOrganizationDtoMapper userOrganizationDtoMapper;

    public UserOrganisationController(
            UserOrganizationService userOrganizationService, UserService userService, OrganizationService organizationService,
            UserOrganizationDtoMapper userOrganizationDtoMapper
    ) {
        this.userOrganizationService = userOrganizationService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.userOrganizationDtoMapper = userOrganizationDtoMapper;
    }

    @PostMapping("/request/join")
    public ResponseEntity<UserOrganizationDto> userJoinOrganization(@RequestBody UserOrganizationIdsDto dto) {
        User user = userService.getUserById(dto.getUserId());
        Organization organization = organizationService.getOrganizationById(dto.getOrganizationId());
        UserOrganizationDto returnDto = userOrganizationDtoMapper.toDto(userOrganizationService.userJoinOrganization(user, organization));
        return new ResponseEntity<>(returnDto, HttpStatus.CREATED);
    }
}
