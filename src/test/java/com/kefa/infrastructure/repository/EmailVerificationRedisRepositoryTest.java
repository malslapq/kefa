package com.kefa.infrastructure.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationRedisRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailVerificationRedisRepository emailVerificationRedisRepository;

    @Test
    @DisplayName("이메일 토큰 저장 성공")
    void saveEmailTokenSuccess() {
        // given
        String token = "test-token";
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        emailVerificationRedisRepository.saveEmailToken(token, email);

        // then
        verify(valueOperations).set(
            eq("EMAIL_VERIFY_" + token),
            eq(email),
            eq(10 * 60L),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("토큰으로 이메일 조회 성공")
    void getEmailByTokenSuccess() {
        // given
        String token = "test-token";
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("EMAIL_VERIFY_" + token)).thenReturn(email);

        // when
        String result = emailVerificationRedisRepository.getEmailByToken(token);

        // then
        assertThat(result).isEqualTo(email);
        verify(valueOperations).get("EMAIL_VERIFY_" + token);
    }

    @Test
    @DisplayName("이메일 토큰 삭제 성공")
    void removeEmailTokenSuccess() {
        // given
        String token = "test-token";

        // when
        emailVerificationRedisRepository.removeEmailToken(token);

        // then
        verify(redisTemplate).delete("EMAIL_VERIFY_" + token);
    }
}