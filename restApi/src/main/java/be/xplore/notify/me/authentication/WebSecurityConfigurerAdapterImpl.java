package be.xplore.notify.me.authentication;

import be.xplore.notify.me.mappers.user.UserDtoMapper;
import be.xplore.notify.me.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
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

@Component
public class WebSecurityConfigurerAdapterImpl extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserServiceSecurityAdapter userServiceSecurityAdapter;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    private final String jwtKey;
    private final String jwtHeader;
    private final String validTime;

    public WebSecurityConfigurerAdapterImpl(
            PasswordEncoder passwordEncoder,
            UserServiceSecurityAdapter userServiceSecurityAdapter,
            ObjectMapper objectMapper,
            UserService userService,
            UserDtoMapper userDtoMapper,
            @Value("${notify.me.security.jwt.key}")
            String jwtKey,
            @Value("${notify.me.security.jwt.httpheader:Authorization}")
            String jwtHeader,
            @Value("${notify.me.security.jwt.validtime:10000000}")
            String validTime
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userServiceSecurityAdapter = userServiceSecurityAdapter;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.jwtKey = jwtKey;
        this.jwtHeader = jwtHeader;
        this.validTime = validTime;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers(HttpMethod.POST, "/authentication/register").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new AuthenticationFilter(authenticationManager(), userService, userDtoMapper, objectMapper, jwtKey, jwtHeader, Long.valueOf(validTime)))
            .addFilter(new AuthorizationFilter(authenticationManager(), jwtHeader, jwtKey));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceSecurityAdapter).passwordEncoder(passwordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
