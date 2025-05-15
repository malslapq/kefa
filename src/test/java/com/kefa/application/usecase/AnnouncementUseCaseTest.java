package com.kefa.application.usecase;

import com.kefa.api.dto.announcement.command.GetAnnouncementsCommand;
import com.kefa.api.dto.announcement.response.AnnouncementResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.domain.entity.Announcement;
import com.kefa.common.type.AnnouncementStatus;
import com.kefa.common.type.AnnouncementsSearchType;
import com.kefa.infrastructure.repository.AnnouncementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AnnouncementUseCaseTest {

    @InjectMocks
    private AnnouncementUseCase announcementUseCase;

    @Mock
    private AnnouncementRepository announcementRepository;

    private final int page = 0;
    private final int size = 10;
    private final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    private Announcement announcement = Announcement.builder()
        .title("제목")
        .department("소관부처")
        .executionAgency("사업수행기관")
        .overview("사업 개요")
        .applicationMethod("신청 방법")
        .contact("문의처")
        .originalLink("원문 링크")
        .tag("태그")
        .status(AnnouncementStatus.ONGOING)
        .createdAt(LocalDateTime.now())
        .build();

    @DisplayName("검색 조건 없는 전체 검색 - 성공")
    @Test
    void getAllAnnouncements() {
        // given
        GetAnnouncementsCommand command = new GetAnnouncementsCommand(null, null, page, size);
        Page<Announcement> pageResult = new PageImpl<>(List.of(announcement));

        given(announcementRepository.findAll(pageable)).willReturn(pageResult);

        // when
        PagedResponse<AnnouncementResponse> response = announcementUseCase.getAnnouncements(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("제목");
    }

    @DisplayName("제목 검색 - 성공")
    @Test
    void getAnnouncementsByTitle() {
        // given
        GetAnnouncementsCommand command = new GetAnnouncementsCommand("제목", AnnouncementsSearchType.TITLE.getType(), page, size);
        Page<Announcement> pageResult = new PageImpl<>(List.of(announcement));

        given(announcementRepository.findByTitleContaining("제목", pageable)).willReturn(pageResult);

        // when
        PagedResponse<AnnouncementResponse> response = announcementUseCase.getAnnouncements(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("제목");
    }

    @DisplayName("태그 검색 - 성공")
    @Test
    void getAnnouncementsByTag() {
        // given
        GetAnnouncementsCommand command = new GetAnnouncementsCommand("태그", AnnouncementsSearchType.TAG.getType(), page, size);
        Page<Announcement> pageResult = new PageImpl<>(List.of(announcement));

        given(announcementRepository.findByTagContaining("태그", pageable)).willReturn(pageResult);

        // when
        PagedResponse<AnnouncementResponse> response = announcementUseCase.getAnnouncements(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTag()).isEqualTo("태그");
    }

    @DisplayName("검색어는 있으나 검색 타입이 잘못된 경우 전체 검색")
    @Test
    void getAnnouncementsWithInvalidSearchType() {
        // given
        GetAnnouncementsCommand command = new GetAnnouncementsCommand("공고", "아무거나", page, size);
        Page<Announcement> pageResult = new PageImpl<>(Collections.singletonList(announcement));

        given(announcementRepository.findAll(pageable)).willReturn(pageResult);

        // when
        PagedResponse<AnnouncementResponse> response = announcementUseCase.getAnnouncements(command);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }



}