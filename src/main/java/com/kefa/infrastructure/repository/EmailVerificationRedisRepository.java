package com.kefa.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long EMAIL_TOKEN_EXPIRATION = 10 * 60;
    private static final String EMAIL_VERIFY_PREFIX = "EMAIL_VERIFY_";

    public void saveEmailToken(String token, String email) {

        redisTemplate.opsForValue()
            .set(
                EMAIL_VERIFY_PREFIX + token,
                email,
                EMAIL_TOKEN_EXPIRATION,
                TimeUnit.SECONDS
            );

    }

    public String getEmailByToken(String token) {
        return redisTemplate.opsForValue().get(EMAIL_VERIFY_PREFIX + token);
    }

    public void removeEmailToken(String token) {
        redisTemplate.delete(EMAIL_VERIFY_PREFIX + token);
    }
}