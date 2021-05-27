package be.xplore.notify.me.services.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PasswordServiceTest {

    @Autowired
    private PasswordService passwordService;

    @Test
    void generatePasswordHashAndCheck() {
        String password = "ThisIsATest";
        String hash = passwordService.generatePasswordHash(password);
        assertTrue(passwordService.checkPassword(password, hash));
    }
}