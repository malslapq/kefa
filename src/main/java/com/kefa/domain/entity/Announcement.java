package com.kefa.domain.entity;

import com.kefa.domain.type.AnnouncementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "announcement")
@EntityListeners(AuditingEntityListener.class)
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String title;

    @Column
    private String department; // 소관부처

    @Column
    private String executionAgency; // 사업수행기관

    @Column
    private LocalDate startDate; // 신청 시작일

    @Column
    private LocalDate endDate; // 신청 마감일

    @Column(columnDefinition = "TEXT")
    private String overview; // 사업 개요

    @Column(length = 1000)
    private String applicationMethod; // 신청 방법

    @Column(length = 500)
    private String contact; // 문의처

    @Column(length = 1000)
    private String originalLink; // 원문 링크

    @Builder.Default
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private List<DownloadFile> downloadFiles = new ArrayList<>();

    @Column
    private String tag;

    @Enumerated(EnumType.STRING)
    private AnnouncementStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

}
