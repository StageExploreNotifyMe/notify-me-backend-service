package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.entity.VenueEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class VenueEntityMapperTest {

    @Autowired
    private VenueEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        Venue object = Venue.builder().id("ThisIsATest").venueManagers(new ArrayList<>()).lineManagers(new ArrayList<>()).build();
        VenueEntity entity = mapper.toEntity(object);
        doEntityAsserts(object, entity);

        Venue fromEntity = mapper.fromEntity(entity);
        doObjectAsserts(object, fromEntity);
    }

    @Test
    void toAndFromEntityWithNulls() {
        Venue object = Venue.builder().id("ThisIsATest").venueManagers(null).lineManagers(null).build();
        VenueEntity entity = mapper.toEntity(object);
        doEntityAsserts(object, entity);

        entity.setVenueManagers(null);
        entity.setLineManagers(null);
        Venue fromEntity = mapper.fromEntity(entity);
        doObjectAsserts(object, fromEntity);
    }

    private void doEntityAsserts(Venue object, VenueEntity entity) {
        assertNotNull(entity);
        assertEquals(object.getId(), entity.getId());
    }

    private void doObjectAsserts(Venue object, Venue fromEntity) {
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }

}