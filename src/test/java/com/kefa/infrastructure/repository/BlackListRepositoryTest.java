package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class BlackListRepositoryTest {

    private BlackListRepository blackListRepository;
    private Cache<String, Boolean> blackListCache;

    private final String jwtId1 = "testJwtId1";
    private final String jwtId2 = "testJwtId2";

    @BeforeEach
    void setUp() {
        blackListCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(100)
            .build();

        blackListRepository = new BlackListRepository(blackListCache);
    }

    @Test
    @DisplayName("단일 JWT ID를 블랙리스트에 추가")
    void saveSingleJwtId() {
        // when
        blackListRepository.save(jwtId1);

        // then
        assertThat(blackListRepository.isBlacklisted(jwtId1)).isTrue();
        assertThat(blackListCache.asMap()).containsKey(jwtId1);
        assertThat(blackListCache.asMap().get(jwtId1)).isTrue();
    }

    @Test
    @DisplayName("여러 JWT ID를 블랙리스트에 추가")
    void saveAllJwtIds() {
        // given
        Set<String> jwtIdsToSave = new HashSet<>();
        String jwtId3 = "testJwtId3";
        String jwtId4 = "testJwtId4";

        jwtIdsToSave.add(jwtId2);
        jwtIdsToSave.add(jwtId3);

        // when
        blackListRepository.saveAll(jwtIdsToSave);

        // then
        assertThat(blackListRepository.isBlacklisted(jwtId2)).isTrue();
        assertThat(blackListRepository.isBlacklisted(jwtId3)).isTrue();
        assertThat(blackListRepository.isBlacklisted(jwtId4)).isFalse();
        assertThat(blackListCache.asMap()).hasSize(2);
    }

    @Test
    @DisplayName("블랙리스트에 없는 JWT ID 조회 시 false 반환")
    void isBlacklistedReturnsFalseForNonExistent() {
        // given
        blackListRepository.save(jwtId1);

        // when
        boolean result = blackListRepository.isBlacklisted(jwtId2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("캐시 만료 시간 테스트: 블랙리스트에 추가된 JWT ID가 만료 후 제거")
    void cacheExpirationTest() throws InterruptedException {
        // given
        blackListCache = Caffeine.newBuilder()
            .expireAfterWrite(100, TimeUnit.MILLISECONDS)
            .maximumSize(10)
            .build();
        blackListRepository = new BlackListRepository(blackListCache);

        blackListRepository.save(jwtId1);
        assertThat(blackListRepository.isBlacklisted(jwtId1)).isTrue();

        // when
        TimeUnit.MILLISECONDS.sleep(100);

        // then
        assertThat(blackListRepository.isBlacklisted(jwtId1)).isFalse();
        assertThat(blackListCache.asMap()).doesNotContainKey(jwtId1);
    }

}