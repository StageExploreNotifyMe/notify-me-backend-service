package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.UserOrganization;
import be.xplore.notify.me.entity.user.UserOrganizationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserOrganizationEntityMapperTest {

    @Autowired
    private UserOrganizationEntityMapper mapper;

    @Test
    void toAndFromEntity() {
        UserOrganization object = UserOrganization.builder().id("500").build();

        UserOrganizationEntity entity = mapper.toEntity(object);
        assertNotNull(entity);
        assertEquals(Long.parseLong(object.getId()), entity.getId());

        UserOrganization fromEntity = mapper.fromEntity(entity);
        assertNotNull(fromEntity);
        assertEquals(fromEntity.getId(), object.getId());
    }
}