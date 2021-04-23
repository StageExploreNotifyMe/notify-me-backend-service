package be.xplore.notify.me.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
