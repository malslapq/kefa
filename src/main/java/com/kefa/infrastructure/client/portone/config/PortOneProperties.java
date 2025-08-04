package com.kefa.infrastructure.client.portone.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "port-one")
@Configuration
@Getter
@Setter
public class PortOneProperties {

    private String apiKey;
    private String apiSecret;
    private String url;

}
