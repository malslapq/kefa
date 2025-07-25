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

    /**
     * Creates a Caffeine cache for storing email verification tokens with a 10-minute expiration and a maximum of 2000 entries.
     *
     * @return a cache mapping email addresses or identifiers to verification tokens
     */
    @Bean
    public Cache<String, String> emailVerificationCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(2000L)
            .build();
    }

    /**
     * Creates a Caffeine cache for mapping email addresses to tokens with a 10-minute expiration and a maximum of 2000 entries.
     *
     * @return a cache storing email-to-token mappings with time-based eviction
     */
    @Bean
    public Cache<String, String> emailToTokenCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(2000L)
            .build();
    }

    /**
     * Creates a Caffeine cache for password reset tokens with a 5-minute expiration and a maximum of 1,000 entries.
     *
     * @return a cache storing password reset tokens keyed by string identifiers
     */
    @Bean
    public Cache<String, String> passwordResetCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000L)
            .build();
    }

    /**
     * Creates a Caffeine LoadingCache for storing active tokens per user ID, with automatic expiration and size limits.
     *
     * Each cache entry maps a user ID (Long) to a concurrent set of token strings. Entries expire after the configured access expiration time in milliseconds and the cache holds up to 100,000 entries. If a user ID is missing, the cache initializes it with an empty concurrent set.
     *
     * @return a LoadingCache mapping user IDs to sets of active token strings
     */
    @Bean
    public LoadingCache<Long, Set<String>> activeTokensCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(accessExpirationTime, TimeUnit.MILLISECONDS)
            .maximumSize(100000L)
            .build(key -> ConcurrentHashMap.newKeySet());
    }

    /**
     * Creates a Caffeine cache for storing blacklisted tokens with a configurable expiration time and maximum size.
     *
     * @return a cache mapping token strings to their blacklist status
     */
    @Bean
    public Cache<String, Boolean> blackListCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(accessExpirationTime, TimeUnit.MILLISECONDS)
            .maximumSize(100000L)
            .build();
    }

    /**
     * Creates and configures a CaffeineCacheManager with predefined cache names and default cache settings.
     *
     * The cache manager registers caches for announcements, email verification, email-to-token mapping, password reset, active tokens, and blacklists.
     * All caches use a default configuration of 1-hour expiration after write, a maximum size of 1000 entries, and statistics recording enabled.
     *
     * @return a configured CacheManager instance for use with Spring's caching abstraction
     */
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
