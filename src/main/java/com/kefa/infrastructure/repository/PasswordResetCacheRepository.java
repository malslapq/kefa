package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PasswordResetCacheRepository {

    /**
     * Constructs a repository for managing password reset tokens using the provided Caffeine cache.
     *
     * @param passwordResetCache the Caffeine cache instance for storing token-email pairs
     */
    public PasswordResetCacheRepository(@Qualifier("passwordResetCache") Cache<String, String> passwordResetCache) {
        this.passwordResetCache = passwordResetCache;
    }

    private final Cache<String, String> passwordResetCache;

    /**
     * Stores the association between a password reset token and an email address in the cache.
     *
     * @param token the password reset token
     * @param email the email address to associate with the token
     */
    public void save(String token, String email) {
        passwordResetCache.put(token, email);
    }

    /**
     * Removes the password reset token and its associated email from the cache.
     *
     * @param token the password reset token to remove
     */
    public void delete(String token) {
        passwordResetCache.invalidate(token);
    }

    /**
     * Retrieves the email address associated with the given password reset token from the cache.
     *
     * @param token the password reset token to look up
     * @return an {@code Optional} containing the associated email if present; otherwise, an empty {@code Optional}
     */
    public Optional<String> findByToken(String token) {
        return Optional.ofNullable(passwordResetCache.getIfPresent(token));
    }

}
