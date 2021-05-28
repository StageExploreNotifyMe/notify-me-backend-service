package be.xplore.notify.me.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private final String header;
    private final String key;
    private final String AUTHORITIES_KEY = "ROLES";

    public AuthorizationFilter(AuthenticationManager authenticationManager, String header, String key) {
        super(authenticationManager);
        this.header = header;
        this.key = key;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(this.header);

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = authenticate(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken authenticate(HttpServletRequest request) {
        String token = request.getHeader(header);

        Claims user = Jwts.parser()
                .setSigningKey(this.key)
                .parseClaimsJws(token)
                .getBody();

        String authoritiesString = user.get(AUTHORITIES_KEY).toString();

        Collection<? extends GrantedAuthority> authorities = null;
        if (authoritiesString.length() > 0) {
            authorities =
                Arrays.stream(authoritiesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = Collections.emptyList();
        }

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}
