package com.kefa.infrastructure.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    @Value("${jwt.key}")
    private String key;
    @Value("${jwt.access-expiration-time}")
    private long accessExpirationTime;
    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

}