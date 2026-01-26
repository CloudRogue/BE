package org.example.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ObMapperConfig {

    @Bean
    public ObjectMapper authObjectMapper() {
        return new ObjectMapper();
    }
}
