package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {



    /**
 * Retrieves a paginated list of notices whose titles contain the specified keyword.
 *
 * @param keyword the substring to search for within notice titles
 * @param pageable pagination and sorting information
 * @return a page of notices with titles containing the keyword
 */
Page<Notice> findByTitleContaining(String keyword, Pageable pageable);
    /**
 * Retrieves a paginated list of notices whose content contains the specified keyword.
 *
 * @param keyword the substring to search for within the content of notices
 * @param pageable pagination and sorting information
 * @return a page of notices with content matching the keyword
 */
Page<Notice> findByContentContaining(String keyword, Pageable pageable);
    /**
 * Retrieves a paginated list of notices whose associated account's name contains the specified keyword.
 *
 * @param keyword the substring to search for within account names
 * @param pageable pagination and sorting information
 * @return a page of notices with account names containing the keyword
 */
Page<Notice> findByAccount_NameContaining(String keyword, Pageable pageable);

}
