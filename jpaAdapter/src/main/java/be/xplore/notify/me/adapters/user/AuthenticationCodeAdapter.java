package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.entity.user.AuthenticationCodeEntity;
import be.xplore.notify.me.mappers.user.AuthenticationMapper;
import be.xplore.notify.me.persistence.AuthenticationCodeRepo;
import be.xplore.notify.me.repositories.JpaAuthenticationCodeRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthenticationCodeAdapter implements AuthenticationCodeRepo {
    private final JpaAuthenticationCodeRepo authenticationCodeRepo;
    private final AuthenticationMapper authenticationMapper;

    public AuthenticationCodeAdapter(JpaAuthenticationCodeRepo authenticationCodeRepo, AuthenticationMapper authenticationMapper) {
        this.authenticationCodeRepo = authenticationCodeRepo;
        this.authenticationMapper = authenticationMapper;
    }

    @Override
    public List<AuthenticationCode> saveAll(List<AuthenticationCode> authenticationCodes) {
        List<AuthenticationCodeEntity> authenticationCodeEntity = authenticationCodeRepo.saveAll(authenticationCodes.stream()
                .map(authenticationMapper::toEntity)
                .collect(Collectors.toList()));
        return authenticationCodeEntity.stream().map(authenticationMapper::fromEntity).collect(Collectors.toList());
    }
}
