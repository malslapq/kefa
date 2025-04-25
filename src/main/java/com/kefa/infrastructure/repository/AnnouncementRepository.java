package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByTitleContaining(String keyword, Pageable pageable);

    Page<Announcement> findByTagContaining(String keyword, Pageable pageable);

}
