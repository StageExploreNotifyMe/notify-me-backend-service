package be.xplore.notify.me.api.event;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventCreationDto;
import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.user.UserService;
import be.xplore.notify.me.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;
    @MockBean
    private VenueService venueService;

    @Autowired
    private Event event;
    @Autowired
    private Venue venue;

    @Autowired
    private User user;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        user.getRoles().remove(Role.ADMIN);
        given(userService.getById(any())).willReturn(user);
    }

    @Test
    void createEvent() {
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().plusMonths(1), "Test Event", venue.getId());
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, eventCreationDto, "/event");
            assertCreateEventSuccess(eventCreationDto, resultActions);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createEventNoAdminRole() {
        TestUtils.setRoles(new Role[]{Role.VENUE_MANAGER});
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().plusMonths(1), "Test Event", venue.getId());
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, eventCreationDto, "/event");
            assertCreateEventSuccess(eventCreationDto, resultActions);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
        TestUtils.setRoles(Role.values());
    }

    @Test
    void createEventNoRoles() {
        TestUtils.setRoles(new Role[]{});
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().plusMonths(1), "Test Event", venue.getId());
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, eventCreationDto, "/event");
            TestUtils.expectStatus(resultActions, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
        TestUtils.setRoles(Role.values());
    }

    private void assertCreateEventSuccess(EventCreationDto eventCreationDto, ResultActions resultActions) throws Exception {
        TestUtils.expectStatus(resultActions, HttpStatus.CREATED);
        EventDto eventDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventDto.class);
        assertEquals(eventCreationDto.getVenueId(), eventDto.getVenue().getId());
        assertEquals(eventCreationDto.getEventDateTime(), eventDto.getDate());
        assertEquals(EventStatus.CREATED, eventDto.getEventStatus());
    }

    @Test
    void createEventUnknownVenue() {
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().plusMonths(1), "Test Event", "qsdf");
        try {
            TestUtils.expectStatus(TestUtils.performPost(mockMvc, eventCreationDto, "/event"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createEventDateIncorrect() {
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().minusMonths(1), "Test Event", venue.getId());
        try {
            TestUtils.expectStatus(TestUtils.performPost(mockMvc, eventCreationDto, "/event"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getEventsOfVenue() {
        mockServices();
        try {
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/event/venue/" + event.getVenue().getId());
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getEventsOfVenueWithPage() {
        mockServices();
        try {
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/event/venue/" + event.getVenue().getId() + "?page=0");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void cancelEvent() {
        try {
            mockServices();
            ResultActions resultActions = TestUtils.performPost(mockMvc, new EventDto(), "/event/" + event.getId() + "/cancel");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventDto eventDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventDto.class);
            assertEquals(EventStatus.CANCELED, eventDto.getEventStatus());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void publishEvent() {
        try {
            mockServices();
            ResultActions resultActions = TestUtils.performPost(mockMvc, new EventDto(), "/event/" + event.getId() + "/publish");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventDto eventDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventDto.class);
            assertEquals(EventStatus.PUBLIC, eventDto.getEventStatus());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void makeEventPrivate() {
        try {
            mockServices();
            ResultActions resultActions = TestUtils.performPost(mockMvc, new EventDto(), "/event/" + event.getId() + "/private");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventDto eventDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventDto.class);
            assertEquals(EventStatus.PRIVATE, eventDto.getEventStatus());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getEventById() {
        try {
            mockServices();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/event/" + event.getId());
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventDto eventDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventDto.class);
            assertEquals(event.getId(), eventDto.getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getEventByIdNotFound() {
        try {
            mockServices();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/event/qsdmflkj");
            TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockServices() {
        mockCreateEvent();
        mockGetEvents();
        mockGetById();
        mockCancelEvent();
        mockPublishEvent();
        mockMakeEventPrivate();
    }

    private void mockGetById() {
        mockGetVenueById();
        mockGetEventById();
    }

    private void mockGetEventById() {
        given(eventService.findById(any())).will(i -> i.getArgument(0).equals(event.getId()) ? Optional.of(event) : Optional.empty());
        given(eventService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(event.getId())) {
                return event;
            }
            throw new NotFoundException("Not found");
        });
    }

    private void mockGetVenueById() {
        given(venueService.findById(any())).will(i -> i.getArgument(0).equals(venue.getId()) ? Optional.of(venue) : Optional.empty());
        given(venueService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(venue.getId())) {
                return venue;
            }
            throw new NotFoundException("Not found");
        });
    }

    private void mockCancelEvent() {
        given(eventService.cancelEvent(any())).will(i -> {
            Event e = i.getArgument(0);
            return Event.builder().id(e.getId()).date(e.getDate()).eventStatus(EventStatus.CANCELED).venue(e.getVenue()).name(e.getName()).build();
        });
    }

    private void mockPublishEvent() {
        given(eventService.publishEvent(any())).will(i -> {
            Event e = i.getArgument(0);
            return Event.builder().id(e.getId()).date(e.getDate()).eventStatus(EventStatus.PUBLIC).venue(e.getVenue()).name(e.getName()).build();
        });
    }

    private void mockMakeEventPrivate() {
        given(eventService.makeEventPrivate(any())).will(i -> {
            Event e = i.getArgument(0);
            return Event.builder().id(e.getId()).date(e.getDate()).eventStatus(EventStatus.PRIVATE).venue(e.getVenue()).name(e.getName()).build();
        });
    }

    private void mockCreateEvent() {
        given(eventService.createEvent(any(LocalDateTime.class), any(String.class), any(Venue.class))).will(i -> {
            LocalDateTime eventDate = i.getArgument(0);
            if (LocalDateTime.now().isAfter(eventDate)) {
                throw new IllegalArgumentException("Wrong date");
            }
            return Event.builder().id("1").eventStatus(EventStatus.CREATED).venue(i.getArgument(2)).name(i.getArgument(1)).date(eventDate).build();
        });
    }

    private void mockGetEvents() {
        given(eventService.getEventsOfVenue(any(String.class), any(int.class))).will(i -> {
            List<Event> entityList = new ArrayList<>();
            if (i.getArgument(0).equals(event.getVenue().getId())) {
                entityList.add(event);
            }
            return new PageImpl<>(entityList);
        });
    }

}
