package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.EventLineRepo;
import be.xplore.notify.me.persistence.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class EventLineServiceTest {

    @Autowired
    private EventLineService eventLineService;
    @Autowired
    private Line line;
    @Autowired
    private User user;
    @Autowired
    private EventLine eventLine;
    @Autowired
    private Organization organization;

    @MockBean
    private EventLineRepo eventLineRepo;
    @MockBean
    private UserRepo userRepo;

    @Test
    void getAllLinesOfEvent() {
        mockGetLinesByEventId();
        Page<EventLine> allByVenue = eventLineService.getAllLinesOfEvent(eventLine.getEvent().getId(), 0);
        assertEquals(eventLine.getId(), allByVenue.getContent().get(0).getId());
    }

    @Test
    void addLineToEvent() {
        mockSave();
        EventLine eventLine = eventLineService.addLineToEvent(line, this.eventLine.getEvent(), user);
        assertEquals(eventLine.getEvent().getId(), eventLine.getEvent().getId());
    }

    @Test
    void assignOrganizationToLine() {
        mockSave();
        EventLine toAssingTo = EventLine.builder().id("qdf").line(line).event(eventLine.getEvent()).lineManager(user).build();
        EventLine saved = eventLineService.assignOrganizationToLine(organization, toAssingTo);
        assertEquals(organization.getId(), saved.getOrganization().getId());
    }

    @Test
    void getById() {
        mockFindById();
        Optional<EventLine> eventLineOptional = eventLineService.getById(eventLine.getId());
        assertTrue(eventLineOptional.isPresent());
        assertEquals(eventLine.getId(), eventLineOptional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        mockFindById();
        Optional<EventLine> eventLineOptional = eventLineService.getById("qdsf");
        assertTrue(eventLineOptional.isEmpty());
    }

    @Test
    void cancelEventLine() {
        mockSave();
        mockSaveUsers();
        EventLine eventLine = eventLineService.cancelEventLine(this.eventLine);
        assertEquals(eventLine.getEventLineStatus(), EventLineStatus.CANCELED);
    }

    @Test
    void assignUserToEventLine() {
        mockSave();
        EventLine eventLineWithUser = eventLineService.assignUserToEventLine(user, this.eventLine);
        assertTrue(eventLineWithUser.getAssignedUsers().stream().anyMatch(u -> u.getId().equals(user.getId())));
        assertEquals(1, eventLineWithUser.getAssignedUsers().size());
        eventLineService.assignUserToEventLine(user, this.eventLine);
        assertEquals(1, eventLineWithUser.getAssignedUsers().size());
    }

    @Test
    void getAllLinesOfOrganization() {
        mockGetLinesOfOrg();
        Page<EventLine> allLinesOfOrganization = eventLineService.getAllLinesOfOrganization(organization.getId(), 0);
        Assertions.assertEquals(1, allLinesOfOrganization.getContent().size());
    }

    @Test
    void getAllLinesOfUser() {
        mockGetEventLinesOfUser();
        Page<EventLine> allLinesOfUser = eventLineService.getAllLinesOfUser(user, 0);
        Assertions.assertEquals(1, allLinesOfUser.getContent().size());
    }

    @Test
    void cancelUserEventLineNotAssigned() {
        assertThrows(IllegalArgumentException.class, () -> eventLineService.cancelUserEventLine(user.getId(), eventLine));
    }

    private EventLine updateAssignedUsers(EventLine line, List<User> users) {
        return EventLine.builder()
            .id(line.getId())
            .line(line.getLine())
            .event(line.getEvent())
            .eventLineStatus(line.getEventLineStatus())
            .lineManager(line.getLineManager())
            .assignedUsers(users)
            .organization(line.getOrganization())
            .lineManager(line.getLineManager())
            .build();
    }

    @Test
    void cancelUserEventLine() {
        mockSave();
        List<User> users = new ArrayList<>();
        users.add(user);
        EventLine eventLine = eventLineService.cancelUserEventLine(user.getId(), updateAssignedUsers(this.eventLine, users));
        assertTrue(eventLine.getAssignedUsers().stream().noneMatch(u -> u.getId().equals(user.getId())));
    }

    private void mockGetEventLinesOfUser() {
        given(eventLineRepo.getAllLinesOfUser(any(), any())).will(i -> getPageOfEventLine());
    }

    private Object getPageOfEventLine() {
        List<EventLine> lineEntityList = new ArrayList<>();
        lineEntityList.add(eventLine);
        return new PageImpl<>(lineEntityList);
    }

    private void mockGetLinesOfOrg() {
        given(eventLineRepo.getAllLinesOfOrganization(any(), any())).will(i -> getPageOfEventLine());
    }

    private void mockSave() {
        given(eventLineRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockGetLinesByEventId() {
        given(eventLineRepo.getAllLinesOfEvent(any(), any())).will(i -> {
            List<EventLine> lineEntityList = new ArrayList<>();
            if (i.getArgument(0).equals(eventLine.getEvent().getId())) {
                lineEntityList.add(eventLine);
            }
            return new PageImpl<>(lineEntityList);
        });
    }

    private void mockFindById() {
        given(eventLineRepo.findById(any())).will(i -> i.getArgument(0).equals(eventLine.getId()) ? Optional.of(eventLine) : Optional.empty());
    }

    private void mockSaveUsers() {
        given(userRepo.save(any())).will(i -> i.getArgument(0));
    }
}