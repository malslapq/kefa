package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.SocialInfo;
import com.kefa.domain.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialInfoRepository extends JpaRepository<SocialInfo, Long> {

    boolean existsByProviderUserIdAndLoginType(String providerUserId, LoginType loginType);

}
