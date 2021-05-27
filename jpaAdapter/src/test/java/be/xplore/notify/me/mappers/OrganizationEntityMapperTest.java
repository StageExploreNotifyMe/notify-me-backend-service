package be.xplore.notify.me.mappers;

import be.xplore.notify.me.domain.Organization;
import be.xplore.notify.me.entity.OrganizationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OrganizationEntityMapperTest {

    @Autowired
    private OrganizationEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        Organization object = Organization.builder().id("ThisIsATest").build();

        OrganizationEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(object.getId(), entity.getId());

        Organization fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}