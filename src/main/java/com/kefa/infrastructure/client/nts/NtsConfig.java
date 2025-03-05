package com.kefa.infrastructure.client.nts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class NtsConfig {

    @Bean
    public RestClient ntsRestClient(RestClient.Builder builder, NtsApiProperties properties) {
        return builder
            .baseUrl(properties.getBaseUrl())
            .build();
    }

}
