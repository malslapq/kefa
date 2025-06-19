package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.SocialInfo;
import com.kefa.common.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialInfoRepository extends JpaRepository<SocialInfo, Long> {

    boolean existsByProviderUserIdAndLoginType(String providerUserId, LoginType loginType);

    @Query("SELECT si FROM SocialInfo si JOIN FETCH si.account WHERE si.providerUserId = :providerUserId")
    Optional<SocialInfo> findByProviderUserIdWithAccount(String providerUserId);
}
