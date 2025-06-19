package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationInMemoryRepositoryTest {

    @Mock
    private Cache<String, String> emailTokenCache;

    @InjectMocks
    private EmailVerificationInMemoryRepository emailVerificationInMemoryRepository;

    private final String token = "test-token";
    private final String email = "test@example.com";

    @Test
    @DisplayName("이메일 토큰 저장 성공")
    void saveEmailTokenSuccess() {
        // given


        // when
        emailVerificationInMemoryRepository.saveEmailToken(token, email);

        // then
        verify(emailTokenCache).put(token, email);
    }

    @Test
    @DisplayName("토큰으로 이메일 조회 성공")
    void findByEmailTokenSuccess() {
        // given
        when(emailTokenCache.getIfPresent(token)).thenReturn(email);

        // when
        String result = emailVerificationInMemoryRepository.findByEmailToken(token);

        // then
        assertThat(result).isEqualTo(email);
        verify(emailTokenCache).getIfPresent(token);
    }

    @Test
    @DisplayName("이메일 토큰 삭제 성공")
    void deleteByEmailTokenSuccess() {
        // given

        // when
        emailVerificationInMemoryRepository.deleteByEmailToken(token);

        // then
        verify(emailTokenCache).invalidate(token);
    }
}