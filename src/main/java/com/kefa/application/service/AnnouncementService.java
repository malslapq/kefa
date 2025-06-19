package com.kefa.application.service;

import com.kefa.api.dto.announcement.command.GetAnnouncementsCommand;
import com.kefa.api.dto.announcement.response.AnnouncementResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.AnnouncementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementUseCase announcementUseCase;

    @Transactional(readOnly = true)
    public PagedResponse<AnnouncementResponse> getAnnouncements(GetAnnouncementsCommand command) {
        return announcementUseCase.getAnnouncements(command);
    }

}
