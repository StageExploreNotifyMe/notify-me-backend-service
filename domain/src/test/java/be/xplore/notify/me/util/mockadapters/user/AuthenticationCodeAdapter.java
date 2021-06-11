package be.xplore.notify.me.util.mockadapters.user;

import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.persistence.AuthenticationCodeRepo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationCodeAdapter implements AuthenticationCodeRepo {
    @Override
    public List<AuthenticationCode> saveAll(List<AuthenticationCode> authenticationCodes) {
        return null;
    }
}
