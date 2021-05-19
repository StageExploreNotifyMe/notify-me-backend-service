package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.organization.CreateOrganizationDto;
import be.xplore.notify.me.dto.organization.OrganizationDto;
import be.xplore.notify.me.mappers.OrganizationDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.user.UserOrganizationService;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.data.domain.Page;
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

import java.util.Optional;

import static be.xplore.notify.me.util.Converters.getPageNumber;

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
        Optional<Organization> optionalOrganization = organizationService.getById(id);
        if (optionalOrganization.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(organizationDtoMapper.toDto(optionalOrganization.get()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationDto>> getOrganizations(@RequestParam(required = false) Integer page) {
        Page<Organization> organizationPage = organizationService.getOrganizations(getPageNumber(page));
        return new ResponseEntity<>(organizationPage.map(organizationDtoMapper::toDto), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Organization> createOrganizations(@RequestBody CreateOrganizationDto createOrganizationDto) {
        User user = getUser(createOrganizationDto.getUserId());
        Organization organization = organizationService.createOrganization(createOrganizationDto.getOrganizationName());
        userOrganizationService.addOrganizationLeaderToOrganization(organization, user);
        return new ResponseEntity<>(organization, HttpStatus.OK);
    }

    private User getUser(String id) {
        Optional<User> optionalUser = userService.getById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user with id " + id + " found");
        }
        return optionalUser.get();
    }
}
