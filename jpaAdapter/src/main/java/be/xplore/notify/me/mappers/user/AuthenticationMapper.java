package be.xplore.notify.me.mappers.user;

import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.entity.user.AuthenticationCodeEntity;
import be.xplore.notify.me.mappers.EntityMapper;
import be.xplore.notify.me.util.LongParser;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapper implements EntityMapper<AuthenticationCodeEntity, AuthenticationCode> {

    @Override
    public AuthenticationCode fromEntity(AuthenticationCodeEntity authenticationCodeEntity) {
        if (authenticationCodeEntity == null) {
            return null;
        }
        return AuthenticationCode.builder()
            .id(String.valueOf(authenticationCodeEntity.getId()))
            .notificationChannel(authenticationCodeEntity.getNotificationChannel())
            .creationDate(authenticationCodeEntity.getCreationDate())
            .code(authenticationCodeEntity.getCode())
            .build();
    }

    @Override
    public AuthenticationCodeEntity toEntity(AuthenticationCode authenticationCode) {
        return new AuthenticationCodeEntity(LongParser.parseLong(authenticationCode.getId()),
            authenticationCode.getCode(),
            authenticationCode.getNotificationChannel(),
            authenticationCode.getCreationDate());
    }
}
