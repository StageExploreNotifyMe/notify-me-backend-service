package be.xplore.notify.me.adapters.user;

import be.xplore.notify.me.domain.user.AuthenticationCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthenticationCodeAdapterTest {

    @Autowired
    AuthenticationCodeAdapter authenticationCodeAdapter;

    @Test
    void saveAll() {
        AuthenticationCode authenticationCode = AuthenticationCode.builder().code("1").build();
        List<AuthenticationCode> authenticationCodes = new ArrayList<>();
        authenticationCodes.add(authenticationCode);
        authenticationCodeAdapter.saveAll(authenticationCodes);
        assertTrue(authenticationCodes.stream().anyMatch(a -> a.getCode().equals(authenticationCode.getCode())));
    }
}