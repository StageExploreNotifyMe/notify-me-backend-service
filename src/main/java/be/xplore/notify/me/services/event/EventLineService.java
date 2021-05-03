package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import be.xplore.notify.me.repositories.EventLineRepo;
import be.xplore.notify.me.services.RepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class EventLineService extends RepoService<EventLine, EventLineEntity> {
    private final EventLineRepo eventLineRepo;

    public EventLineService(EventLineRepo repo, EntityMapper<EventLineEntity, EventLine> entityMapper) {
        super(repo, entityMapper);
        eventLineRepo = repo;
    }

    public Page<EventLine> getAllLinesOfEvent(String eventId, int page) {
        try {
            Page<EventLineEntity> lineEntityPage = eventLineRepo.getAllByEvent_IdOrderByLine(eventId, PageRequest.of(page, 20));
            return lineEntityPage.map(entityMapper::fromEntity);
        } catch (Exception e) {
            log.error("Something went wrong while fetching lines of event {}: {}: {}", eventId, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public EventLine addLineToEvent(Line line, Event event) {
        return save(EventLine.builder().event(event).line(line).assignedUsers(new ArrayList<>()).build());
    }

    public EventLine assignOrganizationToLine(Organization organization, EventLine line) {
        EventLine updatedLine = EventLine.builder()
                .id(line.getId())
                .line(line.getLine())
                .organization(organization)
                .event(line.getEvent())
                .assignedUsers(new ArrayList<>())
                .build();
        return save(updatedLine);
    }
}
