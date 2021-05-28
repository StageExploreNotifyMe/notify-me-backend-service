package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.organization.CreateOrganizationDto;
import be.xplore.notify.me.dto.organization.OrganizationDto;
import be.xplore.notify.me.mappers.OrganizationDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import be.xplore.notify.me.services.user.UserService;
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

import static be.xplore.notify.me.util.ApiUtils.getPageNumber;

@RestController
@RequestMapping(value = "/organization", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationDtoMapper organizationDtoMapper;
    private final UserOrganizationService userOrganizationService;
    private final UserService userService;

    public OrganizationController(
            OrganizationService organizationService,
            OrganizationDtoMapper organizationDtoMapper,
            UserOrganizationService userOrganizationService,
            UserService userService
    ) {
        this.organizationService = organizationService;
        this.organizationDtoMapper = organizationDtoMapper;
        this.userOrganizationService = userOrganizationService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganizationById(@PathVariable String id) {
        return new ResponseEntity<>(organizationDtoMapper.toDto(organizationService.getById(id)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationDto>> getOrganizations(@RequestParam(required = false) Integer page) {
        Page<Organization> organizationPage = organizationService.getOrganizations(getPageNumber(page));
        return new ResponseEntity<>(organizationPage.map(organizationDtoMapper::toDto), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Organization> createOrganizations(@RequestBody CreateOrganizationDto createOrganizationDto, Authentication authentication) {
        ApiUtils.requireRole(authentication, Role.ADMIN);
        User user = userService.getById(createOrganizationDto.getUserId());
        Organization organization = organizationService.createOrganization(createOrganizationDto.getOrganizationName());
        userOrganizationService.addOrganizationLeaderToOrganization(organization, user);
        return new ResponseEntity<>(organization, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<OrganizationDto> updateOrganization(@RequestBody OrganizationDto dto, Authentication authentication) {
        ApiUtils.requireRole(authentication, Role.ADMIN);
        Organization organization = organizationService.updateOrganization(organizationDtoMapper.fromDto(dto));
        return new ResponseEntity<>(organizationDtoMapper.toDto(organization), HttpStatus.OK);
    }
}
