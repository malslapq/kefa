package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerificationInMemoryRepository {

    private final Cache<String, String> emailTokenCache;

    public void saveEmailToken(String token, String email) {
        emailTokenCache.put(token, email);
    }

    public String findByEmailToken(String token) {
        return emailTokenCache.getIfPresent(token);
    }

    public void deleteByEmailToken(String token) {
        emailTokenCache.invalidate(token);
    }
}