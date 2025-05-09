package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.SocialInfo;
import com.kefa.domain.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialInfoRepository extends JpaRepository<SocialInfo, Long> {

    boolean existsByProviderUserIdAndLoginType(String providerUserId, LoginType loginType);

    Optional<SocialInfo> findByProviderUserId(String providerUserId);
}
