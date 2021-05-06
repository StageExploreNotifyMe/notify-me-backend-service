package be.xplore.notify.me.api;

import be.xplore.notify.me.api.util.Converters;
import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.dto.OrganizationDto;
import be.xplore.notify.me.dto.mappers.OrganizationDtoMapper;
import be.xplore.notify.me.services.OrganizationService;
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
@RequestMapping(value = "/organization", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganizationController {
    private final OrganizationService organizationService;
    private final OrganizationDtoMapper organizationDtoMapper;
    private final Converters converters;

    public OrganizationController(OrganizationService organizationService, OrganizationDtoMapper organizationDtoMapper, Converters converters) {
        this.organizationService = organizationService;
        this.organizationDtoMapper = organizationDtoMapper;
        this.converters = converters;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable String id) {
        Optional<Organization> optionalOrganization = organizationService.getById(id);
        if (optionalOrganization.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalOrganization.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationDto>> getOrganizations(@RequestParam(required = false) Integer page) {
        Page<Organization> organizationPage = organizationService.getOrganizations(converters.getPageNumber(page));
        return new ResponseEntity<>(organizationPage.map(organizationDtoMapper::toDto), HttpStatus.OK);
    }
}
