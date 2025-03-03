package com.kefa.infrastructure.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cipher")
@Getter
@Setter
public class CipherProperties {

    private String secretKey;
    private String iv;

}


