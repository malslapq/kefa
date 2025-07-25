package com.kefa.application.usecase;

import com.kefa.api.dto.announcement.command.GetAnnouncementsCommand;
import com.kefa.api.dto.announcement.response.AnnouncementResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.domain.entity.Announcement;
import com.kefa.common.type.AnnouncementsSearchType;
import com.kefa.infrastructure.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "announcements", key = "#command")
    public PagedResponse<AnnouncementResponse> getAnnouncements(GetAnnouncementsCommand command) {

        Pageable pageable = PageRequest.of(
            command.getPage(),
            command.getSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Announcement> announcements = getAnnouncementsFromSearchType(command.getKeyword(), command.getSearchType(), pageable);

        return PagedResponse.<AnnouncementResponse>builder()
            .content(announcements.map(AnnouncementResponse::from).getContent())
            .page(announcements.getNumber())
            .size(announcements.getSize())
            .totalElements(announcements.getTotalElements())
            .totalPages(announcements.getTotalPages())
            .build();
    }

    /**
     * Retrieves a paginated list of announcements filtered by the specified search type and keyword.
     *
     * If the search type is "TITLE", returns announcements whose titles contain the keyword.
     * If the search type is "TAG", returns announcements whose tags contain the keyword.
     * If the search type is "TOTAL", returns announcements whose titles or tags contain the keyword; if the keyword is empty, returns all announcements.
     * If the search type is invalid or the keyword is empty, returns all announcements.
     *
     * @param keyword     the search keyword to filter announcements
     * @param searchType  the type of search to perform ("TITLE", "TAG", or "TOTAL")
     * @param pageable    pagination and sorting information
     * @return a page of announcements matching the search criteria
     */
    private Page<Announcement> getAnnouncementsFromSearchType(String keyword, String searchType, Pageable pageable) {

        AnnouncementsSearchType announcementsSearchTypeFromEnum = AnnouncementsSearchType.from(searchType);

        if (!StringUtils.hasText(keyword) || announcementsSearchTypeFromEnum == null) {
            return announcementRepository.findAll(pageable);
        }

        return switch (announcementsSearchTypeFromEnum) {
            case TITLE -> announcementRepository.findByTitleContaining(keyword, pageable);
            case TAG -> announcementRepository.findByTagContaining(keyword, pageable);
            case TOTAL -> {
                if (StringUtils.hasText(keyword)) {
                    yield announcementRepository.findByTitleContainingOrTagContaining(keyword, keyword, pageable);
                } else {
                    yield announcementRepository.findAll(pageable);
                }
            }
        };
    }

}
