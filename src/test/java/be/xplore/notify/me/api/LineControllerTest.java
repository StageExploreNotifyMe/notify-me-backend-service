package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.event.LineAssignEventDto;
import be.xplore.notify.me.dto.event.LineAssignOrganizationDto;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.event.LineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class LineControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Line line;
    @Autowired
    private EventLine eventLine;
    @Autowired
    private Event event;
    private EventLine eventLineWithoutOrg;

    @MockBean
    private LineService lineService;
    @MockBean
    private EventLineService eventLineService;
    @MockBean
    private EventService eventService;
    @MockBean
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        eventLineWithoutOrg = EventLine.builder().id("50").event(eventLine.getEvent()).line(eventLine.getLine()).assignedUsers(new ArrayList<>()).build();
    }

    @Test
    void getLinesOfVenue() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/venue/" + line.getVenue().getId());
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getLinesOfVenueWithPage() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/venue/" + line.getVenue().getId() + "?page=0");
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getLinesOfVenueNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/venue/mqldfkj");
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getEventLines() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/event/" + eventLine.getEvent().getId());
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getEventLinesEventNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/event/qdsf");
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignLineToEvent() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/event/add", new LineAssignEventDto(line.getId(), event.getId()));
            expectResult(resultActions, HttpStatus.CREATED);
            EventLineDto eventLineDto = mapper.readValue(getResponse(resultActions), EventLineDto.class);
            assertEquals(event.getId(), eventLineDto.getEvent().getId());
            assertEquals(line.getId(), eventLineDto.getLine().getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignLineToEventLineNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/event/add", new LineAssignEventDto("qdsf", event.getId()));
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignOrganizationToEventLine() {
        try {
            mockEverything();
            given(eventLineService.getById(any())).will(i -> i.getArgument(0).equals(eventLineWithoutOrg.getId()) ? Optional.of(eventLineWithoutOrg) : Optional.empty());
            ResultActions resultActions = performPost("/line/" + eventLineWithoutOrg.getId() + "/assign/organization",
                    new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), eventLine.getOrganization().getId())
            );
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignOrganizationToEventLineBadRequest() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/qdsf/assign/organization",
                    new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), eventLine.getOrganization().getId())
            );
            expectResult(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignOrganizationToEventLineNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/qdsf/assign/organization", new LineAssignOrganizationDto("qdsf", eventLine.getOrganization().getId()));
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignOrganizationToEventLineOrgNotFound() {
        try {
            mockEverything();
            given(eventLineService.getById(any()))
                    .will(i -> i.getArgument(0).equals(eventLineWithoutOrg.getId()) ? Optional.of(eventLineWithoutOrg) : Optional.empty());
            ResultActions resultActions =
                    performPost("/line/" + eventLineWithoutOrg.getId() + "/assign/organization", new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), "dfj"));
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void mockEverything() {
        mockGetEventLinesByEvent();
        mockGetEventById();
        mockGetLinesByVenue();
        mockGetLineById();
        mockAddLineToEvent();
        mockGetOrganizationById();
        mockAssingOrgToLine();
    }

    private void mockAssingOrgToLine() {
        given(eventLineService.assignOrganizationToLine(any(), any())).will(i -> {
            Organization org = i.getArgument(0);
            EventLine el = i.getArgument(1);
            return EventLine.builder().line(el.getLine()).event(el.getEvent()).id(el.getId()).organization(org).assignedUsers(el.getAssignedUsers()).build();
        });
    }

    private void mockGetOrganizationById() {
        given(organizationService.getById(any()))
                .will(i -> i.getArgument(0).equals(eventLine.getOrganization().getId()) ? Optional.of(eventLine.getOrganization()) : Optional.empty());
    }

    private void mockAddLineToEvent() {
        given(eventLineService.addLineToEvent(any(), any())).willReturn(eventLine);
    }

    private void mockGetLinesByVenue() {
        given(lineService.getAllByVenue(any(), any(int.class))).will(i -> {
            List<Line> lineList = new ArrayList<>();
            if (i.getArgument(0).equals(line.getVenue().getId())) {
                lineList.add(line);
            }
            return new PageImpl<>(lineList);
        });
    }

    private void mockGetEventLinesByEvent() {
        given(eventLineService.getAllLinesOfEvent(any(), any(int.class))).will(i -> {
            List<EventLine> lineList = new ArrayList<>();
            if (i.getArgument(0).equals(eventLine.getEvent().getId())) {
                lineList.add(eventLine);
            }
            return new PageImpl<>(lineList);
        });
    }

    private void mockGetEventById() {
        given(eventService.getById(any())).will(i -> i.getArgument(0).equals(event.getId()) ? Optional.of(event) : Optional.empty());
    }

    private void mockGetLineById() {
        given(lineService.getById(any())).will(i -> i.getArgument(0).equals(line.getId()) ? Optional.of(line) : Optional.empty());
    }

    private ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
            .content(mapper.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON));
    }

    private void expectResult(ResultActions resultActions, HttpStatus ok) throws Exception {
        resultActions.andExpect(status().is(ok.value()));
    }

    private String getResponse(ResultActions resultActions) throws UnsupportedEncodingException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }
}