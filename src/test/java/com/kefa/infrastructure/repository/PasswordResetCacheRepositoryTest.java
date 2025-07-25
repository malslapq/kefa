package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class PasswordResetCacheRepositoryTest {

    private PasswordResetCacheRepository passwordResetCacheRepository;
    private Cache<String, String> passwordResetCache;
    private final String testToken1 = "testToken1";
    private final String testEmail1 = "test@test.com";


    @BeforeEach
    void setUp() {
        passwordResetCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

        passwordResetCacheRepository = new PasswordResetCacheRepository(passwordResetCache);
    }

    @Test
    @DisplayName("비밀번호 초기화 토큰 저장 - 성공")
    void saveAndRetrieveToken() {
        // given

        // when
        passwordResetCacheRepository.save(testToken1, testEmail1);

        // then
        Optional<String> foundEmail = passwordResetCacheRepository.findByToken(testToken1);
        assertThat(foundEmail).isPresent();
        assertThat(foundEmail.get()).isEqualTo(testEmail1);
        assertThat(passwordResetCache.asMap()).containsKey(testToken1);
    }

    @Test
    @DisplayName("비밀번호 초기화 토큰 조회 - 실패")
    void findByTokenReturnsEmptyForNonExistent() {
        // given

        // when
        Optional<String> foundEmail = passwordResetCacheRepository.findByToken("notToken");

        // then
        assertThat(foundEmail).isEmpty();
        assertThat(passwordResetCache.asMap()).doesNotContainKey("notToken");
    }

    @Test
    @DisplayName("비밀번호 초기화 토큰 제거 - 성공")
    void deleteRemovesToken() {
        // given
        passwordResetCacheRepository.save(testToken1, testEmail1);
        assertThat(passwordResetCacheRepository.findByToken(testToken1)).isPresent();

        // when
        passwordResetCacheRepository.delete(testToken1);

        // then
        Optional<String> foundEmail = passwordResetCacheRepository.findByToken(testToken1);
        assertThat(foundEmail).isEmpty();
        assertThat(passwordResetCache.asMap()).doesNotContainKey(testToken1);
    }

    @Test
    @DisplayName("비밀번호 초기화 토큰 캐시 만료 조회 시 Optional.empty 반환")
    void tokenExpiresAfterWrite() throws InterruptedException {
        // given
        passwordResetCache = Caffeine.newBuilder()
            .expireAfterWrite(100, TimeUnit.MILLISECONDS)
            .maximumSize(10)
            .build();
        passwordResetCacheRepository = new PasswordResetCacheRepository(passwordResetCache);

        String testEmail2 = "test@test.com";
        String testToken2 = "testToken2";
        passwordResetCacheRepository.save(testToken2, testEmail2);
        assertThat(passwordResetCacheRepository.findByToken(testToken2)).isPresent();

        // when
        TimeUnit.MILLISECONDS.sleep(150);

        // then
        Optional<String> foundEmail = passwordResetCacheRepository.findByToken(testToken2);
        assertThat(foundEmail).isEmpty();
        assertThat(passwordResetCache.asMap()).doesNotContainKey(testToken2);
    }

}