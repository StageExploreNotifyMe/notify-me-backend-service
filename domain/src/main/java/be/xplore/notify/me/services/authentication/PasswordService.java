package be.xplore.notify.me.services.authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String generatePasswordHash(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(String plainTextPassword, String passwordHash) {
        return passwordEncoder.matches(plainTextPassword, passwordHash);
    }
}
