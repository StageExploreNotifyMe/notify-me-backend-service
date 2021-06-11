package be.xplore.notify.me.api.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.event.EventLineDto;
import be.xplore.notify.me.dto.event.StaffingReminderDto;
import be.xplore.notify.me.dto.line.LineAssignEventDto;
import be.xplore.notify.me.dto.line.LineAssignOrganizationDto;
import be.xplore.notify.me.dto.line.LineMemberDto;
import be.xplore.notify.me.services.OrganizationService;
import be.xplore.notify.me.services.event.EventLineService;
import be.xplore.notify.me.services.event.EventService;
import be.xplore.notify.me.services.event.LineService;
import be.xplore.notify.me.services.user.UserOrganizationService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

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
    private final User user2 = User.builder().id("2").roles(new HashSet<>(Collections.singletonList(Role.MEMBER))).build();
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
    @MockBean
    private UserOrganizationService userOrganizationService;

    @BeforeEach
    void setUp() {
        eventLineWithoutOrg = EventLine.builder().id("50").event(eventLine.getEvent()).line(eventLine.getLine()).assignedUsers(new ArrayList<>()).build();
        mockEverything();
        mockGetUser();
        TestUtils.reset();
    }

    @Test
    void getEventLines() {
        ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/event/" + eventLine.getEvent().getId());
        TestUtils.expectStatus(resultActions, HttpStatus.OK);
    }

    @Test
    void getEventLinesEventNotFound() {
        ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/event/qdsf");
        TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void assignLineToEvent() {
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, new LineAssignEventDto(line.getId(), event.getId(), user.getId()), "/line/event/add");
            TestUtils.expectStatus(resultActions, HttpStatus.CREATED);
            EventLineDto eventLineDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventLineDto.class);
            assertEquals(event.getId(), eventLineDto.getEvent().getId());
            assertEquals(line.getId(), eventLineDto.getLine().getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void assignLineToEventLineNotFound() {
        ResultActions resultActions = TestUtils.performPost(mockMvc, new LineAssignEventDto("qdsf", event.getId(), user.getId()), "/line/event/add");
        TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void assignLineToEventLineManagerNotFound() {
        ResultActions resultActions = TestUtils.performPost(mockMvc, new LineAssignEventDto(line.getId(), event.getId(), "qsdf"), "/line/event/add");
        TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void assignOrganizationToEventLine() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), eventLine.getOrganization().getId()), "/line/" + eventLineWithoutOrg.getId() + "/assign/organization"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.OK);
    }

    @Test
    void assignOrganizationToEventLineBadRequest() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), eventLine.getOrganization().getId()), "/line/qdsf/assign/organization"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
    }

    @Test
    void assignOrganizationToEventLineNotFound() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineAssignOrganizationDto("qdsf", eventLine.getOrganization().getId()),
                "/line/qdsf/assign/organization"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void assignOrganizationToEventLineOrgNotFound() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineAssignOrganizationDto(eventLineWithoutOrg.getId(), "dfj"),
                "/line/" + eventLineWithoutOrg.getId() + "/assign/organization"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void assignMemberToEventLine() {
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc,
                    new LineMemberDto(user.getId(), eventLine.getOrganization().getId()), "/line/" + eventLine.getId() + "/assign/member"
            );
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventLineDto eventLineDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventLineDto.class);
            assertTrue(eventLineDto.getAssignedUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void assignMemberToEventLineMemberNotFound() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineMemberDto(eventLine.getId(), "qdsf"),
                "/line/" + eventLine.getId() + "/assign/member"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
    }

    @Test
    void assignMemberToEventLineBadRequest() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineMemberDto(user.getId(), eventLine.getOrganization().getId()),
                "/line/qsdf/assign/member"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
    }

    @Test
    void getEventLine() {
        try {
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/" + event.getId());
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventLineDto eventLineDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventLineDto.class);
            assertEquals(eventLine.getId(), eventLineDto.getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getEventLinesOfOrganization() {
        ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/organization/" + organization.getId() + "?page=1");
        TestUtils.expectStatus(resultActions, HttpStatus.OK);
    }

    @Test
    void cancelMemberEventLine() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineMemberDto(eventLine.getId(), user.getId()),
                "/line/" + eventLine.getId() + "/cancel/member"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.OK);
    }

    @Test
    void cancelMemberEventLineBadRequest() {
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineMemberDto(eventLine.getId(), user.getId()),
                "/line/qsdf/cancel/member"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
    }

    @Test
    void cancelMemberEventLineWrongMember() {
        given(userOrganizationService.getAllUserOrganizationsByUserId(any())).willReturn(new ArrayList<>());
        TestUtils.setUserId("2");
        TestUtils.setRoles(new Role[]{Role.MEMBER});
        ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineMemberDto(eventLine.getId(), user.getId()),
                "/line/" + eventLine.getId() + "/cancel/member"
        );
        TestUtils.expectStatus(resultActions, HttpStatus.UNAUTHORIZED);
    }

    @Test
    void cancelEventLine() {
        try {
            mockCancelEventLine();
            ResultActions resultActions = TestUtils.performPost(mockMvc, new EventLineDto(), "/line/" + eventLine.getId() + "/cancel");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            EventLineDto eventLineDto = mapper.readValue(TestUtils.getContentAsString(resultActions), EventLineDto.class);
            assertEquals(EventLineStatus.CANCELED, eventLineDto.getEventLineStatus());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void sendStaffingReminder() {
        try {
            ResultActions resultActions = TestUtils.performPost(mockMvc, new StaffingReminderDto(eventLine.getId(), null), "/line/" + eventLine.getId() + "/staffingreminder");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            StaffingReminderDto dto = mapper.readValue(TestUtils.getContentAsString(resultActions), StaffingReminderDto.class);
            assertEquals(eventLine.getId(), dto.getEventLineId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockEverything() {
        mockGetEventLinesByEvent();
        mockGetLinesByVenue();
        mockAssignOrgToLine();
        mockGetByIds();
        mockAssignUserToLine();
        mockGetLinesOfOrg();
        given(eventLineService.addLineToEvent(any(), any(), any())).willReturn(eventLine);
        given(eventLineService.cancelUserEventLine(any(), any(EventLine.class))).willReturn(eventLine);
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

    private void mockGetByIds() {
        mockGetEventLineById();
        mockGetLineById();
        mockGetEventById();
        mockGetOrganizationById();
    }

    private void mockGetOrganizationById() {
        given(organizationService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(organization.getId())) {
                return organization;
            }
            throw new NotFoundException("");
        });
    }

    private void mockGetEventById() {
        given(eventService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(event.getId())) {
                return event;
            }
            throw new NotFoundException("");
        });
    }

    private void mockGetLineById() {
        given(lineService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(line.getId())) {
                return line;
            }
            throw new NotFoundException("");
        });
    }

    private void mockGetUser() {
        given(userService.getById(any())).will(i -> {
            if (i.getArgument(0).equals(user.getId())) {
                return user;
            } else if (i.getArgument(0).equals(user2.getId())) {
                return user2;
            }
            throw new NotFoundException("");
        });
    }

    private void mockGetEventLineById() {
        given(eventLineService.getById(any())).will(i -> {
            Optional<EventLine> byId = eventLineService.findById(i.getArgument(0));
            if (byId.isPresent()) {
                return byId.get();
            }
            throw new NotFoundException("");
        });
        mockFindEventLineById();
    }

    private void mockFindEventLineById() {
        given(eventLineService.findById(any())).will(i -> {
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

}