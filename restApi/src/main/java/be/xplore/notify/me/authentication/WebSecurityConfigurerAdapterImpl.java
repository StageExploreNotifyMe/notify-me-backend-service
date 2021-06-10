package be.xplore.notify.me.authentication;

import be.xplore.notify.me.domain.user.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class WebSecurityConfigurerAdapterImpl extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserServiceSecurityAdapter userServiceSecurityAdapter;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    public WebSecurityConfigurerAdapterImpl(
            PasswordEncoder passwordEncoder,
            UserServiceSecurityAdapter userServiceSecurityAdapter,
            ObjectMapper objectMapper,
            JwtService jwtService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userServiceSecurityAdapter = userServiceSecurityAdapter;
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers("/authentication/register").permitAll()
            .antMatchers("/user/**").authenticated()
            .antMatchers("/admin/**").access(getAccessString(Collections.singletonList(Role.ADMIN)))
            .antMatchers("/line/**").access(getAccessString(Arrays.asList(Role.MEMBER, Role.ORGANIZATION_LEADER, Role.LINE_MANAGER, Role.VENUE_MANAGER)))
            .antMatchers("/event/**").access(getAccessString(Arrays.asList(Role.MEMBER, Role.ORGANIZATION_LEADER, Role.LINE_MANAGER, Role.VENUE_MANAGER)))
            .antMatchers("/userorganization/**").access(getAccessString(Arrays.asList(Role.MEMBER, Role.ORGANIZATION_LEADER)))
            .antMatchers("/organization/**").access(getAccessString(Arrays.asList(Role.MEMBER, Role.ORGANIZATION_LEADER, Role.LINE_MANAGER, Role.VENUE_MANAGER)))
            .anyRequest().authenticated()
            .and()
            .addFilter(new AuthenticationFilter(authenticationManager(), objectMapper, jwtService))
            .addFilter(new AuthorizationFilter(authenticationManager(), jwtService));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceSecurityAdapter).passwordEncoder(passwordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.PATCH.toString(), HttpMethod.PUT.toString(), HttpMethod.POST.toString(), HttpMethod.GET.toString(), HttpMethod.DELETE.toString()
        ));
        config.addAllowedHeader("Authorization");

        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private String getAccessString(List<Role> roles) {
        String hasAuthorityString = "hasAuthority(\"ROLE_ADMIN\")";
        if (roles.size() == 0) {
            return hasAuthorityString;
        }
        for (Role role : roles) {
            hasAuthorityString += "or hasAuthority(\"ROLE_" + role + "\")";
        }
        return hasAuthorityString;
    }
}
