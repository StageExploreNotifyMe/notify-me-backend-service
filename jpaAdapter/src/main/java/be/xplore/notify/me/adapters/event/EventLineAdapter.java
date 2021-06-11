package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Event;
import be.xplore.notify.me.domain.event.EventLine;
import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.domain.event.EventStatus;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.entity.event.EventLineEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import be.xplore.notify.me.mappers.event.EventEntityMapper;
import be.xplore.notify.me.mappers.event.EventLineEntityMapper;
import be.xplore.notify.me.mappers.user.UserEntityMapper;
import be.xplore.notify.me.persistence.EventLineRepo;
import be.xplore.notify.me.repositories.JpaEventLineRepo;
import be.xplore.notify.me.util.LongParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EventLineAdapter implements EventLineRepo {
    private final JpaEventLineRepo jpaEventLineRepo;
    private final EventLineEntityMapper eventLineEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final EventEntityMapper eventEntityMapper;

    public EventLineAdapter(
            JpaEventLineRepo jpaEventLineRepo,
            EventLineEntityMapper eventLineEntityMapper,
            UserEntityMapper userEntityMapper,
            EventEntityMapper eventEntityMapper
    ) {
        this.jpaEventLineRepo = jpaEventLineRepo;
        this.eventLineEntityMapper = eventLineEntityMapper;
        this.userEntityMapper = userEntityMapper;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Transactional
    @Override
    public List<User> getLineManagersByEvent(Event event) {
        List<EventLineEntity> eventLineEntity = jpaEventLineRepo.getAllByEvent(eventEntityMapper.toEntity(event));
        List<UserEntity> lineManagersEntity = eventLineEntity.stream().map(EventLineEntity::getLineManager).collect(Collectors.toList());
        return lineManagersEntity.stream().map(userEntityMapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Optional<EventLine> findById(String id) {
        Optional<EventLineEntity> optional = jpaEventLineRepo.findById(LongParser.parseLong(id));
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        EventLine eventLine = eventLineEntityMapper.fromEntity(optional.get());
        return Optional.of(eventLine);
    }

    @Override
    public EventLine save(EventLine eventLine) {
        EventLineEntity eventLineEntity = jpaEventLineRepo.save(eventLineEntityMapper.toEntity(eventLine));
        return eventLineEntityMapper.fromEntity(eventLineEntity);
    }

    @Override
    public Page<EventLine> getAllLinesOfOrganization(String id, PageRequest pageRequest) {
        Page<EventLineEntity> eventLineEntityPage = jpaEventLineRepo.getAllByOrganization_IdOrderByEvent_date(LongParser.parseLong(id), pageRequest);
        return eventLineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    @Override
    public Page<EventLine> getAllLinesOfUser(User user, PageRequest pageRequest) {
        Page<EventLineEntity> eventLineEntityPage = jpaEventLineRepo.getAllByAssignedUsersContainsOrderByEvent_date(userEntityMapper.toEntity(user), pageRequest);
        return eventLineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    @Override
    public Page<EventLine> getAllLinesOfEvent(String eventId, PageRequest pageRequest) {
        Page<EventLineEntity> lineEntityPage = jpaEventLineRepo.getAllByEvent_IdOrderByLine(LongParser.parseLong(eventId), pageRequest);
        return lineEntityPage.map(eventLineEntityMapper::fromEntity);
    }

    @Transactional
    @Override
    public List<EventLine> getAllActiveEventLinesOfLineManager(String lineManagerId) {
        List<EventLineEntity> eventLines =
                jpaEventLineRepo.getAllByLineManager_IdAndEventLineStatusNotAndEvent_EventStatusNotOrderByEvent_date(
                    LongParser.parseLong(lineManagerId), EventLineStatus.CANCELED, EventStatus.CANCELED
                );
        return eventLines.stream().map(eventLineEntityMapper::fromEntity).collect(Collectors.toList());
    }
}
