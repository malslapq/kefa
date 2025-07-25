package com.kefa.infrastructure.repository;

import com.kefa.common.type.ContactStatus;
import com.kefa.domain.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
 * Retrieves a paginated list of contacts filtered by the specified status.
 *
 * @param status   the status to filter contacts by
 * @param pageable pagination and sorting information
 * @return a page of contacts matching the given status
 */
Page<Contact> findByStatus(ContactStatus status, Pageable pageable);

}
