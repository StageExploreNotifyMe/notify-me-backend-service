package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.event.LineAssignEventDto;
import be.xplore.notify.me.dto.event.LineAssignOrganizationDto;
import be.xplore.notify.me.dto.event.LineMemberDto;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.event.LineService;
import be.xplore.notify.me.services.user.UserService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventLineControllerTest {
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
    @Autowired
    private User user;
    @Autowired
    private Organization organization;

    @MockBean
    private LineService lineService;
    @MockBean
    private EventLineService eventLineService;
    @MockBean
    private EventService eventService;
    @MockBean
    private OrganizationService organizationService;
    @MockBean
    private UserService userService;

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
            ResultActions resultActions = performPost("/line/event/add", new LineAssignEventDto(line.getId(), event.getId(), user.getId()));
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
            ResultActions resultActions = performPost("/line/event/add", new LineAssignEventDto("qdsf", event.getId(), user.getId()));
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignLineToEventLineManagerNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/event/add", new LineAssignEventDto(line.getId(), event.getId(), "qsdf"));
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignOrganizationToEventLine() {
        try {
            mockEverything();
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
            ResultActions resultActions =
                    performPost("/line/" + eventLineWithoutOrg.getId() + "/assign/organization", new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), "dfj"));
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignMemberToEventLine() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/" + eventLine.getId() + "/assign/member",
                new LineMemberDto(user.getId(), eventLine.getOrganization().getId())
            );
            expectResult(resultActions, HttpStatus.OK);
            EventLineDto eventLineDto = mapper.readValue(getResponse(resultActions), EventLineDto.class);
            assertTrue(eventLineDto.getAssignedUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignMemberToEventLineMemberNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/" + eventLine.getId() + "/assign/member",
                new LineMemberDto(eventLine.getId(), "qdsf")
            );
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void assignMemberToEventLineBadRequest() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/qsdf/assign/member",
                new LineMemberDto(user.getId(), eventLine.getOrganization().getId())
            );
            expectResult(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getEventLine() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/" + event.getId());
            expectResult(resultActions, HttpStatus.OK);
            EventLineDto eventLineDto = mapper.readValue(getResponse(resultActions), EventLineDto.class);
            assertEquals(eventLine.getId(), eventLineDto.getId());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getEventLinesOfOrganization() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/organization/" + organization.getId() + "?page=1");
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void cancelMemberEventLine() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/" + eventLine.getId() + "/cancel/member",
                new LineMemberDto(eventLine.getId(), user.getId())
            );
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void cancelMemberEventLineBadRequest() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/qsdf/cancel/member",
                new LineMemberDto(eventLine.getId(), user.getId())
            );
            expectResult(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void cancelEventLine() {
        try {
            mockEverything();
            mockCancelEventLine();
            ResultActions resultActions = performPost("/line/" + eventLine.getId() + "/cancel", new EventLineDto());
            expectResult(resultActions, HttpStatus.OK);
            EventLineDto eventLineDto = mapper.readValue(getResponse(resultActions), EventLineDto.class);
            assertEquals(EventLineStatus.CANCELED, eventLineDto.getEventLineStatus());
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void mockEverything() {
        mockGetEventLinesByEvent();
        mockGetLinesByVenue();
        mockAddLineToEvent();
        mockAssignOrgToLine();
        mockGetByIds();
        mockAssignUserToLine();
        mockGetLinesOfOrg();
        mockCancelMemberLine();
    }

    private void mockGetLinesOfOrg() {
        given(eventLineService.getAllLinesOfOrganization(any(), any(int.class))).will(i -> {
            List<EventLine> eventLines = new ArrayList<>();
            eventLines.add(eventLine);
            return new PageImpl<>(eventLines);
        });
    }

    private void mockAssignUserToLine() {
        given(eventLineService.assignUserToEventLine(any(User.class), any(EventLine.class))).will(i -> {
            EventLine line = i.getArgument(1);
            line.getAssignedUsers().add(i.getArgument(0));
            return line;
        });
    }

    private void mockCancelMemberLine() {
        given(eventLineService.cancelUserEventLine(any(), any(EventLine.class))).willReturn(eventLine);
    }

    private void mockGetByIds() {
        given(userService.getById(any())).will(i -> i.getArgument(0).equals(user.getId()) ? Optional.of(user) : Optional.empty());
        mockGetEventLineById();
        given(lineService.getById(any())).will(i -> i.getArgument(0).equals(line.getId()) ? Optional.of(line) : Optional.empty());
        given(eventService.getById(any())).will(i -> i.getArgument(0).equals(event.getId()) ? Optional.of(event) : Optional.empty());
        given(organizationService.getById(any()))
                .will(i -> i.getArgument(0).equals(eventLine.getOrganization().getId()) ? Optional.of(eventLine.getOrganization()) : Optional.empty());
    }

    private void mockGetEventLineById() {
        given(eventLineService.getById(any())).will(i -> {
            String id = i.getArgument(0);
            EventLine line = null;
            if (id.equals(eventLine.getId())) {
                line = eventLine;
            } else if (id.equals(eventLineWithoutOrg.getId())) {
                line = eventLineWithoutOrg;
            }
            return line == null ? Optional.empty() : Optional.of(line);
        });
    }

    private void mockAssignOrgToLine() {
        given(eventLineService.assignOrganizationToLine(any(), any())).will(i -> {
            Organization org = i.getArgument(0);
            EventLine el = i.getArgument(1);
            return EventLine.builder().line(el.getLine()).event(el.getEvent()).id(el.getId()).organization(org).assignedUsers(el.getAssignedUsers()).build();
        });
    }

    private void mockCancelEventLine() {
        given(eventLineService.cancelEventLine(any())).will(i -> {
            EventLine eventLine = i.getArgument(0);
            return EventLine.builder()
                .id(eventLine.getId())
                .eventLineStatus(EventLineStatus.CANCELED)
                .line(eventLine.getLine())
                .event(eventLine.getEvent())
                .assignedUsers(eventLine.getAssignedUsers()).build();
        });
    }

    private void mockAddLineToEvent() {
        given(eventLineService.addLineToEvent(any(), any(), any())).willReturn(eventLine);
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