package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.mappers.event.EventLineEntityMapper;
import be.xplore.notify.me.repositories.EventLineRepo;
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
    private EventLine eventLine;
    @Autowired
    private EventLineEntityMapper eventLineEntityMapper;
    @Autowired
    private Organization organization;
    @Autowired
    private User user;

    @MockBean
    private EventLineRepo eventLineRepo;

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

    private void mockSave() {
        given(eventLineRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockGetLinesByEventId() {
        given(eventLineRepo.getAllByEvent_IdOrderByLine(any(), any())).will(i -> {
            List<EventLineEntity> lineEntityList = new ArrayList<>();
            if (i.getArgument(0).equals(eventLine.getEvent().getId())) {
                lineEntityList.add(eventLineEntityMapper.toEntity(eventLine));
            }
            return new PageImpl<>(lineEntityList);
        });
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

    private void mockFindById() {
        given(eventLineRepo.findById(any())).will(i -> i.getArgument(0).equals(eventLine.getId()) ? Optional.of(eventLineEntityMapper.toEntity(eventLine)) : Optional.empty());
    }
}