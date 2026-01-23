package org.example.announcements.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MypageRestClientConfig {

    @Bean("mypageRestClient")
    public RestClient mypageRestClient(
            @Value("${internal.mypage.base-url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean("aiRestClient")
    public RestClient aiRestClient(
            @Value("${internal.ai.base-url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
