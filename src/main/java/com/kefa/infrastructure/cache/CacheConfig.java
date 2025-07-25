package com.kefa.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@EnableCaching
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
    public Cache<String, String> emailToTokenCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(2000L)
            .build();
    }

    @Bean
    public Cache<String, String> passwordResetCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000L)
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

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCacheNames(Arrays.asList(
            "announcements",
            "emailVerificationCache",
            "emailToTokenCache",
            "passwordResetCache",
            "activeTokensCache",
            "blackListCache"
        ));

        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .recordStats());

        return cacheManager;
    }

}
