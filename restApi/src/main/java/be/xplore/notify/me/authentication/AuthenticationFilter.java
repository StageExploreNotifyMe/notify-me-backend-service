package be.xplore.notify.me.authentication;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.user.LoggedInDto;
import be.xplore.notify.me.dto.user.LoginDto;
import be.xplore.notify.me.mappers.user.UserDtoMapper;
import be.xplore.notify.me.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper mapper;
    private final String jwtKey;
    private final String jwtHeader;
    private final String AUTHORITIES_KEY = "ROLES";
    private final Long jwtValidTime;

    public AuthenticationFilter(
            AuthenticationManager authenticationManager,
            UserService userService,
            UserDtoMapper userDtoMapper,
            ObjectMapper objectMapper,
            String jwtKey,
            String jwtHeader,
            Long jwtValidTime
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.mapper = objectMapper;
        this.userDtoMapper = userDtoMapper;
        this.jwtKey = jwtKey;
        this.jwtHeader = jwtHeader;
        this.jwtValidTime = jwtValidTime;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto loginDto = mapper.readValue(request.getInputStream(), LoginDto.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String userIdentifier = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
        String token = generateJwtToken(userIdentifier);
        addTokenToResponse(response, token, userIdentifier);
    }

    private String generateJwtToken(String userIdentifier) {
        String authorities = getRolesOfUser(getUser(userIdentifier)).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date exp = new Date(System.currentTimeMillis() + jwtValidTime);
        Claims claims = Jwts.claims().setSubject(userIdentifier);
        claims.put(AUTHORITIES_KEY, authorities);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, this.jwtKey).setExpiration(exp).compact();
    }

    private void addTokenToResponse(HttpServletResponse response, String token, String userIdentifier) throws IOException {
        response.addHeader(jwtHeader, token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        addUserDetailsToBody(response, token, userIdentifier);
    }

    private void addUserDetailsToBody(HttpServletResponse response, String token, String userIdentifier) throws IOException {
        PrintWriter writer = response.getWriter();
        Optional<User> user = userService.getById(userIdentifier);
        if (user.isEmpty()) {
            throw new NotFoundException("No user found for " + userIdentifier);
        }
        writer.write(mapper.writeValueAsString(new LoggedInDto(token, userDtoMapper.toDto(user.get()))));
        writer.flush();
        writer.close();
    }

    private Set<SimpleGrantedAuthority> getRolesOfUser(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString())));
        return authorities;
    }

    private User getUser(String id) {
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found associated with this token");
        }
        return userOptional.get();
    }
}
