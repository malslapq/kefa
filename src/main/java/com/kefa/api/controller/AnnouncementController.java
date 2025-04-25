package com.kefa.api.controller;

import com.kefa.api.dto.announcement.command.GetAnnouncementsCommand;
import com.kefa.api.dto.announcement.response.AnnouncementResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.service.AnnouncementService;
import com.kefa.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyRole('FREE_ACCOUNT', 'PAID_ACCOUNT', 'CONCIERGE_ACCOUNT', 'EXPERT', 'ADMIN', 'STAFF')")
@RestController
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/announcements")
    public ApiResponse<PagedResponse<AnnouncementResponse>> getAnnouncements(@RequestParam String keyword,
                                                                             @RequestParam String searchType,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {

        GetAnnouncementsCommand command = GetAnnouncementsCommand.builder()
            .keyword(keyword)
            .searchType(searchType)
            .page(page)
            .size(size)
            .build();

        return ApiResponse.success(announcementService.getAnnouncements(command));
    }

}
