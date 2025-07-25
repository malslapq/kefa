package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PasswordResetCacheRepository {

    public PasswordResetCacheRepository(@Qualifier("passwordResetCache") Cache<String, String> passwordResetCache) {
        this.passwordResetCache = passwordResetCache;
    }

    private final Cache<String, String> passwordResetCache;

    public void save(String token, String email) {
        passwordResetCache.put(token, email);
    }

    public void delete(String token) {
        passwordResetCache.invalidate(token);
    }

    public Optional<String> findByToken(String token) {
        return Optional.ofNullable(passwordResetCache.getIfPresent(token));
    }

}
