package com.kefa.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${jwt.access-expiration-time}")
    private long accessExpirationTime;

    @Bean
    public Cache<String, String> emailVerificationCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(2000L)
            .build();
    }

    @Bean
    public LoadingCache<Long, Set<String>> activeTokensCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(accessExpirationTime, TimeUnit.MILLISECONDS)
            .maximumSize(100000L)
            .build(key -> ConcurrentHashMap.newKeySet());
    }

    @Bean
    public Cache<String, Boolean> blackListCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(accessExpirationTime, TimeUnit.MILLISECONDS)
            .maximumSize(100000L)
            .build();
    }

}
