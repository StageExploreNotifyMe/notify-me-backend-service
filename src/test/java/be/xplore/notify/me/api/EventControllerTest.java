package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.dto.event.EventCreationDto;
import be.xplore.notify.me.dto.event.EventDto;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void createEvent() {
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().plusMonths(1), "Test Event", venue.getId());
        try {
            ResultActions resultActions = performPost(eventCreationDto, "/event");
            expectResult(resultActions, HttpStatus.CREATED);
            EventDto eventDto = mapper.readValue(getResult(resultActions), EventDto.class);
            assertDataCorrectCreatedEvent(eventCreationDto, eventDto);
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void assertDataCorrectCreatedEvent(EventCreationDto eventCreationDto, EventDto eventDto) {
        assertEquals(eventCreationDto.getVenueId(), eventDto.getVenue().getId());
        assertEquals(eventCreationDto.getEventDateTime(), eventDto.getDate());
        assertEquals(EventStatus.CREATED, eventDto.getEventStatus());
    }

    @Test
    void createEventUnknownVenue() {
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().plusMonths(1), "Test Event", "qsdf");
        try {
            expectResult(performPost(eventCreationDto, "/event"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createEventDateIncorrect() {
        mockServices();
        EventCreationDto eventCreationDto = new EventCreationDto(LocalDateTime.now().minusMonths(1), "Test Event", venue.getId());
        try {
            expectResult(performPost(eventCreationDto, "/event"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getEventsOfVenue() {
        mockServices();
        try {
            ResultActions resultActions = performGetVenueEvents("/event/venue/" + event.getVenue().getId());
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getEventsOfVenueWithPage() {
        mockServices();
        try {
            ResultActions resultActions = performGetVenueEvents("/event/venue/" + event.getVenue().getId() + "?page=0");
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void cancelEvent() {
        try {
            mockServices();
            ResultActions resultActions = performPost(new EventDto(), "/event/" + event.getId() + "/cancel");
            expectResult(resultActions, HttpStatus.OK);
            EventDto eventDto = mapper.readValue(getResult(resultActions), EventDto.class);
            assertEquals(EventStatus.CANCELED, eventDto.getEventStatus());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void publishEvent() {
        try {
            mockServices();
            ResultActions resultActions = performPost(new EventDto(), "/event/" + event.getId() + "/publish");
            expectResult(resultActions, HttpStatus.OK);
            EventDto eventDto = mapper.readValue(getResult(resultActions), EventDto.class);
            assertEquals(EventStatus.PUBLIC, eventDto.getEventStatus());
        } catch (Exception e) {
            failTest(e);
        }
    }

    private ResultActions performGetVenueEvents(String url) throws Exception {
        return mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON));
    }

    private void expectResult(ResultActions resultActions, HttpStatus ok) throws Exception {
        resultActions.andExpect(status().is(ok.value()));
    }

    private void mockServices() {
        mockCreateEvent();
        mockGetEvents();
        given(venueService.getById(any())).will(i -> i.getArgument(0).equals(venue.getId()) ? Optional.of(venue) : Optional.empty());
        given(eventService.getById(any())).will(i -> i.getArgument(0).equals(event.getId()) ? Optional.of(event) : Optional.empty());
        mockCancelEvent();
        mockPublishEvent();
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

    private ResultActions performPost(Object body, String url) throws Exception {
        return mockMvc.perform(post(url).content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON));
    }

    private String getResult(ResultActions resultActions) throws UnsupportedEncodingException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

}
