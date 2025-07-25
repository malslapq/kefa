package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {



    Page<Notice> findByTitleContaining(String keyword, Pageable pageable);
    Page<Notice> findByContentContaining(String keyword, Pageable pageable);
    Page<Notice> findByAccount_NameContaining(String keyword, Pageable pageable);

}
