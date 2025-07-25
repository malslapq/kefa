package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
 * Retrieves all companies associated with the specified account ID, ordered by creation date in descending order.
 *
 * @param id the ID of the account whose companies are to be retrieved
 * @return a list of companies linked to the given account, sorted by most recently created first
 */
List<Company> findAllByAccountIdOrderByCreatedAtDesc(Long id);

    /**
     * Retrieves a company by its ID, eagerly loading its associated account.
     *
     * @param id the unique identifier of the company
     * @return an Optional containing the company with its account if found, or empty if not found
     */
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.account WHERE c.id = :id")
    Optional<Company> findCompanyById(Long id);

    /**
 * Checks if a non-deleted company exists with the specified business number.
 *
 * @param businessNumber the business number to search for
 * @return true if a non-deleted company with the given business number exists, false otherwise
 */
boolean existsByBusinessNumberAndDeletedFalse(String businessNumber);
}
