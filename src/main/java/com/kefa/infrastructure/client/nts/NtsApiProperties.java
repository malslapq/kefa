package com.kefa.infrastructure.client.nts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nts.api")
@Getter
@Setter
public class NtsApiProperties {

    private String key;
    private String baseUrl;

}
