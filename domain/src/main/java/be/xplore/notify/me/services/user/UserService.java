package be.xplore.notify.me.services.user;

import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.BadRequestException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.AuthenticationCode;
import be.xplore.notify.me.domain.user.RegistrationStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.persistence.UserRepo;
import be.xplore.notify.me.services.authentication.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepo userRepo;
    private final UserPreferencesService userPreferencesService;
    private final PasswordService passwordService;
    private final AuthenticationCodeService authenticationCodeService;

    public UserService(UserRepo userRepo, UserPreferencesService userPreferencesService, PasswordService passwordService, AuthenticationCodeService authenticationCodeService) {
        this.userRepo = userRepo;
        this.userPreferencesService = userPreferencesService;
        this.passwordService = passwordService;
        this.authenticationCodeService = authenticationCodeService;
    }

    public User addNotificationToInbox(Notification notification, User user) {
        user.getInbox().add(notification);
        return save(user);
    }

    public Page<User> getUsersPage(PageRequest pageRequest) {
        return userRepo.findAll(pageRequest);
    }

    public User addNotificationToQueue(Notification notification, User user) {
        user.getNotificationQueue().add(notification);
        return save(user);
    }

    public User getById(String userId) {
        Optional<User> userOptional = findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found for id " + userId);
        }
        return userOptional.get();
    }

    public Optional<User> findById(String id) {
        return userRepo.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User setNotificationChannels(String userId, NotificationChannel normalChannel, NotificationChannel urgentChannel) {
        userPreferencesService.setNotificationChannels(getById(userId), normalChannel, urgentChannel);
        return getById(userId);
    }

    public User clearUserQueue(User user) {
        User toSave = User.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .userPreferences(user.getUserPreferences())
                .notificationQueue(new ArrayList<>())
                .inbox(user.getInbox())
                .build();
        return save(toSave);
    }

    public User registerNewUser(User user) {
        preRegistrationChecks(user);
        User toSave = User.builder()
                .userPreferences(userPreferencesService.generateDefaultPreferences())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .inbox(new ArrayList<>())
                .notificationQueue(new ArrayList<>())
                .roles(new HashSet<>())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .passwordHash(passwordService.generatePasswordHash(user.getPasswordHash()))
                .registrationStatus(RegistrationStatus.PENDING)
                .authenticationCodes(authenticationCodeService.generateUserAuthCodes())
                .build();

        User savedUser = save(toSave);
        log.trace("Registered a new user: {} {}", savedUser.getFirstname(), savedUser.getLastname());
        authenticationCodeService.sendUserAuthCodes(savedUser, savedUser.getAuthenticationCodes());
        return savedUser;
    }

    public User confirmRegistration(User user, String emailCode, String smsCode) {
        boolean email = user.getAuthenticationCodes().stream().anyMatch(c -> c.getNotificationChannel() == NotificationChannel.EMAIL && c.getCode().equals(emailCode));
        boolean sms = user.getAuthenticationCodes().stream().anyMatch(c -> c.getNotificationChannel() == NotificationChannel.SMS && c.getCode().equals(smsCode));
        if (!email || !sms) {
            throw new BadRequestException("Email or sms code was incorrect");
        }
        User toSave = User.builder()
                .id(user.getId())
                .userPreferences(user.getUserPreferences())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .inbox(user.getInbox())
                .notificationQueue(user.getNotificationQueue())
                .roles(user.getRoles())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .passwordHash(user.getPasswordHash())
                .registrationStatus(RegistrationStatus.OK)
                .authenticationCodes(new ArrayList<>())
                .build();
        return save(toSave);
    }

    public User addRole(User user, Role role) {
        user.getRoles().add(role);
        return save(user);
    }

    public User removeRole(User user, Role role) {
        user.getRoles().remove(role);
        return save(user);
    }

    private void preRegistrationChecks(User user) {
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new AlreadyExistsException("A user with that email is already registered");
        }
    }

    public User setAuthenticationCodes(User user, List<AuthenticationCode> authenticationCodeList) {
        User toSave = User.builder()
                .id(user.getId())
                .userPreferences(user.getUserPreferences())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .inbox(user.getInbox())
                .notificationQueue(user.getNotificationQueue())
                .roles(user.getRoles())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .passwordHash(user.getPasswordHash())
                .registrationStatus(user.getRegistrationStatus())
                .authenticationCodes(authenticationCodeList)
                .build();
        return save(toSave);
    }

    public void requestLoginAuthCode(User user) {
        AuthenticationCode authenticationCode = authenticationCodeService.generateAuthCode(NotificationChannel.EMAIL);
        List<AuthenticationCode> authenticationCodes = authenticationCodeService.saveAll(Collections.singletonList(authenticationCode));
        User updatedUser = setAuthenticationCodes(user, Collections.singletonList(authenticationCodes.get(0)));
        authenticationCodeService.sendUserAuthCode(updatedUser, authenticationCode);
    }

    public List<User> getAllById(ArrayList<String> ids) {
        return userRepo.findAllByIds(ids);
    }
}
