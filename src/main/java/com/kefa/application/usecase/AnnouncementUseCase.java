package com.kefa.application.usecase;

import com.kefa.api.dto.announcement.command.GetAnnouncementsCommand;
import com.kefa.api.dto.announcement.response.AnnouncementResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.domain.entity.Announcement;
import com.kefa.domain.type.SearchType;
import com.kefa.infrastructure.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AnnouncementUseCase {

    private final AnnouncementRepository announcementRepository;

    public PagedResponse<AnnouncementResponse> getAnnouncements(GetAnnouncementsCommand command) {

        Pageable pageable = PageRequest.of(
            command.getPage(),
            command.getSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Announcement> announcements = getAnnouncementsFromSearchType(command, pageable);

        return PagedResponse.<AnnouncementResponse>builder()
            .content(announcements.map(AnnouncementResponse::from).getContent())
            .page(announcements.getNumber())
            .size(announcements.getSize())
            .totalElements(announcements.getTotalElements())
            .totalPages(announcements.getTotalPages())
            .build();
    }

    private Page<Announcement> getAnnouncementsFromSearchType(GetAnnouncementsCommand command, Pageable pageable) {

        String keyword = command.getKeyword();
        SearchType searchType = SearchType.from(command.getSearchType());

        if (!StringUtils.hasText(keyword) || searchType == null) {
            return announcementRepository.findAll(pageable);
        }

        return switch (searchType) {
            case TITLE -> announcementRepository.findByTitleContaining(keyword, pageable);
            case TAG -> announcementRepository.findByTagContaining(keyword, pageable);
            case TOTAL -> announcementRepository.findAll(pageable);
        };
    }

}
