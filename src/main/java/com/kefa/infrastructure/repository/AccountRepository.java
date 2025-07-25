package com.kefa.infrastructure.repository;

import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import com.kefa.domain.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query
    Optional<Account> findByEmail(String email);

    /**
 * Checks if an account exists with the specified email address.
 *
 * @param email the email address to check for existence
 * @return true if an account with the given email exists, false otherwise
 */
boolean existsByEmail(String email);

    /**
     * Retrieves an account by its ID, eagerly fetching associated refresh token and social information.
     *
     * @param id the unique identifier of the account
     * @return an {@code Optional} containing the account with its refresh token and social information if found, or empty if not found
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.refreshToken LEFT JOIN FETCH a.socialInfos s WHERE a.id = :id")
    Optional<Account> findByIdWithAllAssociations(@Param("id") Long id);

    /**
     * Retrieves an account by its ID, eagerly fetching associated social information.
     *
     * @param accountId the ID of the account to retrieve
     * @return an {@code Optional} containing the account with its social information if found, or empty if not found
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.socialInfos WHERE a.id = :accountId")
    Optional<Account> findByIdWithSocialInfos(Long accountId);

    /**
     * Retrieves an account by email, eagerly fetching its associated refresh token.
     *
     * @param email the email address of the account to retrieve
     * @return an Optional containing the account with its refresh token if found, or empty if not found
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.refreshToken WHERE a.email = :email")
    Optional<Account> findByEmailWithRefreshToken(String email);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.refreshToken WHERE a.id = :id")
    Optional<Account> findByIdWithRefreshToken(Long id);

    Page<Account> findByNameContaining(String name, Pageable pageable);

    Page<Account> findByEmailContaining(String email, Pageable pageable);

    Page<Account> findByRole(Role role, Pageable pageable);

    Page<Account> findBySubscriptionType(SubscriptionType subscriptionType, Pageable pageable);
}
