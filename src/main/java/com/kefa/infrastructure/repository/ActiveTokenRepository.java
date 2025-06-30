package com.kefa.infrastructure.repository;

import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ActiveTokenRepository {

    private final LoadingCache<Long, Set<String>> activeTokensCache;

    public void save(Long accountId, String jwtId) {
        findByAccountId(accountId).add(jwtId);
    }

    public void delete(Long accountId, String jwtId) {

        Set<String> activeJwtIds = findByAccountId(accountId);
        activeJwtIds.remove(jwtId);

        if(activeJwtIds.isEmpty()) {
            activeTokensCache.invalidate(accountId);
        }

    }

    public Set<String> findByAccountId(Long accountId) {
        return activeTokensCache.get(accountId);
    }

    public void invalidateAccountAllActiveTokens(Long accountId) {
        activeTokensCache.invalidate(accountId);
    }

}
