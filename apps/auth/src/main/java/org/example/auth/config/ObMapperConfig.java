package org.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class ObMapperConfig {

    @Bean
    public ObjectMapper authObjectMapper() {
        return new ObjectMapper();
    }
}
