package be.xplore.notify.me.authentication;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.user.LoginDto;
import be.xplore.notify.me.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper mapper;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthenticationFilter(
            AuthenticationManager authenticationManager,
            ObjectMapper objectMapper,
            JwtService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.mapper = objectMapper;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto loginDto = mapper.readValue(request.getInputStream(), LoginDto.class);
            User user = getUserByMail(loginDto);
            if (user.getAuthenticationCodes().stream().noneMatch(authCode -> authCode.getCode().equals(loginDto.getAuthCode()))) {
                throw new BadCredentialsException("2FA code not found");
            }
            userService.setAuthenticationCodes(user, new ArrayList<>());

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getId().toLowerCase(), loginDto.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User getUserByMail(LoginDto loginDto) {
        Optional<User> optionalUser = userService.getUserByEmail(loginDto.getId());
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("Could not find a user with that id");
        }
        return optionalUser.get();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String userIdentifier = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
        String token = jwtService.generateJwtToken(userIdentifier);
        jwtService.addTokenToResponse(response, token, userIdentifier);
    }
}
