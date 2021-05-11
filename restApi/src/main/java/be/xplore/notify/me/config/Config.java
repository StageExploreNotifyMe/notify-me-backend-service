package be.xplore.notify.me.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }
}
