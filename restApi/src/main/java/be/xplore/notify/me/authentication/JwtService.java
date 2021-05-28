package be.xplore.notify.me.authentication;

import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.dto.user.LoggedInDto;
import be.xplore.notify.me.mappers.user.UserDtoMapper;
import be.xplore.notify.me.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String AUTHORITIES_KEY = "ROLES";

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final ObjectMapper mapper;

    private final String jwtKey;
    private final String jwtHeader;
    private final String jwtValidTime;

    public JwtService(
            UserService userService,
            UserDtoMapper userDtoMapper,
            ObjectMapper mapper,
            @Value("${notify.me.security.jwt.key}") String jwtKey,
            @Value("${notify.me.security.jwt.httpheader:Authorization}") String jwtHeader,
            @Value("${notify.me.security.jwt.validtime:10000000}") String jwtValidTime
    ) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.mapper = mapper;
        this.jwtKey = jwtKey;
        this.jwtHeader = jwtHeader;
        this.jwtValidTime = jwtValidTime;
    }

    public String getTokenFromHttpServletRequest(HttpServletRequest request) {
        return request.getHeader(jwtHeader);
    }

    public Claims getClaimsFromToken(String jwt) {
        return Jwts.parser()
            .setSigningKey(jwtKey)
            .parseClaimsJws(jwt)
            .getBody();
    }

    public Collection<? extends GrantedAuthority> getGrantedAuthoritiesFromClaims(Claims jwtClaims) {
        String authoritiesString = jwtClaims.get(AUTHORITIES_KEY).toString();

        Collection<? extends GrantedAuthority> authorities;
        if (authoritiesString.length() > 0) {
            authorities = Arrays.stream(authoritiesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = Collections.emptyList();
        }
        return authorities;
    }

    public String generateJwtToken(String userIdentifier) {
        String authorities = getRolesOfUser(userService.getById(userIdentifier)).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date exp = new Date(System.currentTimeMillis() + Long.parseLong(jwtValidTime));
        Claims claims = Jwts.claims().setSubject(userIdentifier);
        claims.put(AUTHORITIES_KEY, authorities);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, this.jwtKey).setExpiration(exp).compact();
    }

    public Set<SimpleGrantedAuthority> getRolesOfUser(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString())));
        return authorities;
    }

    public void addTokenToResponse(HttpServletResponse response, String token, String userIdentifier) throws IOException {
        response.addHeader(jwtHeader, token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        addUserDetailsToBody(response, token, userIdentifier);
    }

    public void addUserDetailsToBody(HttpServletResponse response, String token, String userIdentifier) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write(mapper.writeValueAsString(new LoggedInDto(token, userDtoMapper.toDto(userService.getById(userIdentifier)))));
        writer.flush();
        writer.close();
    }

    public String getJwtHeader() {
        return jwtHeader;
    }
}
