package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BlackListRepository {

    private final Cache<String, Boolean> blackListCache;

    public void save(String jwtId) {
        blackListCache.put(jwtId, true);
    }

    public void saveAll(Set<String> jwtIds) {
        for (String jwtId : jwtIds) {
            blackListCache.put(jwtId, true);
        }
    }

    public boolean isBlacklisted(String jwtId) {
        return blackListCache.getIfPresent(jwtId) != null;
    }

}
