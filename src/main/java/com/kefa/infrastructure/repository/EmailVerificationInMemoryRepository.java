package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerificationInMemoryRepository {

    private final Cache<String, String> emailTokenCache;
    private final Cache<String, String> emailToTokenCache;


    public void saveEmailToken(String token, String email) {
        emailTokenCache.put(token, email);
        emailToTokenCache.put(email, token);
    }

    public String findByEmailToken(String token) {
        return emailTokenCache.getIfPresent(token);
    }

    public void deleteByEmailToken(String token) {
        String email = emailTokenCache.getIfPresent(token);
        emailTokenCache.invalidate(token);
        if (email != null) {
            emailToTokenCache.invalidate(email);
        }
    }

    public void deleteByEmail(String email) {
        String token = emailToTokenCache.getIfPresent(email);
        emailToTokenCache.invalidate(email);
        if (token != null) {
            emailTokenCache.invalidate(token);
        }
    }
}