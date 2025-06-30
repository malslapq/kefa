package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ActiveTokenRepositoryTest {

    private ActiveTokenRepository activeTokenRepository;
    private LoadingCache<Long, Set<String>> activeTokensCache;

    private final Long accountId1 = 1L;
    private final String jwtId1 = "testJwtId1";
    private final String jwtId2 = "testJwtId2";

    @BeforeEach
    void setUp() {
        activeTokensCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build(key -> ConcurrentHashMap.newKeySet());

        activeTokenRepository = new ActiveTokenRepository(activeTokensCache);
    }

    @Test
    @DisplayName("새로운 accountId에 JWT ID 추가")
    void saveNewAccountId() {
        // when
        activeTokenRepository.save(accountId1, jwtId1);

        // then
        Set<String> jwtIds = activeTokenRepository.findByAccountId(accountId1);

        assertThat(jwtIds).isNotNull();
        assertThat(jwtIds).containsExactly(jwtId1);
        assertThat(activeTokensCache.asMap()).containsKey(accountId1);
    }

    @Test
    @DisplayName("기존 accountId에 JWT ID 추가")
    void saveExistingAccountId() {
        // given
        activeTokenRepository.save(accountId1, jwtId1);

        // when
        activeTokenRepository.save(accountId1, jwtId2);

        // then
        Set<String> jwtIds = activeTokenRepository.findByAccountId(accountId1);
        assertThat(jwtIds).isNotNull();
        assertThat(jwtIds).containsExactlyInAnyOrder(jwtId1, jwtId2);
        assertThat(activeTokensCache.asMap()).containsKey(accountId1);
    }

    @Test
    @DisplayName("특정 accountId의 특정 JWT ID 제거")
    void deleteSpecificJwtId() {
        // given
        activeTokenRepository.save(accountId1, jwtId1);
        activeTokenRepository.save(accountId1, jwtId2);

        // when
        activeTokenRepository.delete(accountId1, jwtId1);

        // then
        Set<String> jwtIds = activeTokenRepository.findByAccountId(accountId1);
        assertThat(jwtIds).isNotNull();
        assertThat(jwtIds).doesNotContain(jwtId1);
        assertThat(jwtIds).containsExactly(jwtId2);
        assertThat(activeTokensCache.asMap()).containsKey(accountId1);
    }

    @Test
    @DisplayName("마지막 JWT ID 제거 시 accountId 캐시 데이터 제거")
    void deleteLastJwtIdRemovesEntry() {
        // given
        activeTokenRepository.save(accountId1, jwtId1);

        // when
        activeTokenRepository.delete(accountId1, jwtId1);

        // then
        Set<String> jwtIds = activeTokensCache.getIfPresent(accountId1);
        assertThat(jwtIds).isNull();
        assertThat(activeTokensCache.asMap()).doesNotContainKey(accountId1);
    }

    @Test
    @DisplayName("존재하지 않는 accountId로 조회 시 빈 Set 반환")
    void findByAccountIdNotFound() {
        // when
        Set<String> jwtIds = activeTokenRepository.findByAccountId(999L);

        // then
        assertThat(jwtIds).isNotNull();
        assertThat(jwtIds).isEmpty();
    }

    @Test
    @DisplayName("특정 accountId의 모든 JWT ID 제거")
    void invalidateAccountAllActiveTokens() {
        // given
        activeTokenRepository.save(accountId1, jwtId1);
        activeTokenRepository.save(accountId1, jwtId2);
        Long accountId2 = 2L;
        String jwtId3 = "testJwtId3";
        activeTokenRepository.save(accountId2, jwtId3);

        // when
        activeTokenRepository.invalidateAccountAllActiveTokens(accountId1);

        // then
        Set<String> jwtIds1 = activeTokensCache.getIfPresent(accountId1);
        assertThat(jwtIds1).isNull();
        assertThat(activeTokensCache.asMap()).doesNotContainKey(accountId1);
        Set<String> jwtIds2 = activeTokenRepository.findByAccountId(accountId2);
        assertThat(jwtIds2).containsExactly(jwtId3);
        assertThat(activeTokensCache.asMap()).containsKey(accountId2);
    }

    @Test
    @DisplayName("캐시 만료 시간 테스트 (짧은 만료 시간)")
    void cacheExpirationTest() throws InterruptedException {
        // given
        activeTokensCache = Caffeine.newBuilder()
            .expireAfterWrite(100, TimeUnit.MILLISECONDS)
            .maximumSize(100)
            .build(key -> ConcurrentHashMap.newKeySet());
        activeTokenRepository = new ActiveTokenRepository(activeTokensCache);

        activeTokenRepository.save(accountId1, jwtId1);
        assertThat(activeTokenRepository.findByAccountId(accountId1)).containsExactly(jwtId1);

        // when
        TimeUnit.MILLISECONDS.sleep(150);

        // then
        Set<String> jwtIds = activeTokensCache.getIfPresent(accountId1);
        assertThat(jwtIds).isNull();
        assertThat(activeTokensCache.asMap()).doesNotContainKey(accountId1);
    }

}