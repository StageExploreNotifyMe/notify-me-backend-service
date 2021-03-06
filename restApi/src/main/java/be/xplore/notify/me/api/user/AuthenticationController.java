package be.xplore.notify.me.api.user;

import be.xplore.notify.me.domain.exceptions.BadRequestException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.user.AuthenticationCodeDto;
import be.xplore.notify.me.dto.user.LoginDto;
import be.xplore.notify.me.dto.user.UserDto;
import be.xplore.notify.me.dto.user.UserRegisterDto;
import be.xplore.notify.me.mappers.user.UserDtoMapper;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/authentication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    public AuthenticationController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegisterDto userRegisterDto) {
        User user = userService.registerNewUser(validateAndConvertRegisterDto(userRegisterDto));
        return new ResponseEntity<>(userDtoMapper.toDto(user), HttpStatus.OK);
    }

    @PostMapping("/confirmed")
    public ResponseEntity<UserDto> confirmRegister(@RequestBody AuthenticationCodeDto authenticationCodeDto) {
        User user = userService.getById(authenticationCodeDto.getUserId());
        User updatedUser = userService.confirmRegistration(user, authenticationCodeDto.getEmailCode(), authenticationCodeDto.getSmsCode());
        return new ResponseEntity<>(userDtoMapper.toDto(updatedUser), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> requestLogin(@RequestBody LoginDto loginDto) {
        Optional<User> userByEmail = userService.getUserByEmail(loginDto.getId());
        if (userByEmail.isEmpty()) {
            throw new BadRequestException("No user found with that email");
        }
        userService.requestLoginAuthCode(userByEmail.get());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private User validateAndConvertRegisterDto(UserRegisterDto userRegisterDto) {
        return User.builder()
            .firstname(validateNotEmpty(userRegisterDto.getFirstname(), "Firstname"))
            .lastname(validateNotEmpty(userRegisterDto.getLastname(), "LastName"))
            .passwordHash(validateNotEmpty(userRegisterDto.getPassword(), "Password"))
            .email(validateEmail(userRegisterDto.getEmail().toLowerCase()))
            .mobileNumber(validateNotEmpty(userRegisterDto.getMobileNumber(), "Mobile number"))
            .build();
    }

    private String validateEmail(String email) {
        validateNotEmpty(email, "Email");
        if (!email.contains("@")) {
            throw new BadRequestException("Invalid email");
        }
        if (!email.contains(".")) {
            throw new BadRequestException("Invalid email");
        }
        return email;
    }

    private String validateNotEmpty(String field, String fieldName) {
        if (field.isBlank()) {
            throw new BadRequestException(fieldName + " cannot be empty");
        }
        return field;
    }
}
