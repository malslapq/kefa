package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Account a JOIN FETCH a.socialInfos WHERE a.id = :accountId")
    Optional<Account> findByIdWithSocialInfos(Long accountId);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.refreshToken WHERE a.email = :email")
    Optional<Account> findByEmailWithRefreshToken(String email);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.refreshToken WHERE a.id = :id")
    Optional<Account> findByIdWithRefreshToken(Long id);
}
