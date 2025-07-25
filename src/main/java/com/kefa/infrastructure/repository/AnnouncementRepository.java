package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByTitleContaining(String keyword, Pageable pageable);

    /**
 * Retrieves a paginated list of announcements whose tag contains the specified keyword.
 *
 * @param keyword the substring to search for within the tag field
 * @param pageable pagination information
 * @return a page of announcements matching the tag search criteria
 */
Page<Announcement> findByTagContaining(String keyword, Pageable pageable);

    /**
 * Retrieves a paginated list of announcements where the title contains the specified title keyword or the tag contains the specified tag keyword.
 *
 * @param titleKeyword the keyword to search for within announcement titles
 * @param tagKeyword the keyword to search for within announcement tags
 * @param pageable pagination information
 * @return a page of announcements matching either the title or tag keyword criteria
 */
Page<Announcement> findByTitleContainingOrTagContaining(String titleKeyword, String tagKeyword, Pageable pageable);

}
