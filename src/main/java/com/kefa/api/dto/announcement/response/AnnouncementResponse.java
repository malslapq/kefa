package com.kefa.api.dto.announcement.response;

import com.kefa.domain.entity.Announcement;
import com.kefa.domain.type.AnnouncementStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class AnnouncementResponse {

    private Long id;
    private String title;
    private String department;
    private String executionAgency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String overview;
    private String applicationMethod;
    private String contact;
    private String originalLink;
    private List<DownloadFileResponse> downloadFiles;
    private String tag;
    private AnnouncementStatus status;
    private LocalDateTime createdAt;

    public static AnnouncementResponse from(Announcement announcement) {
        return AnnouncementResponse.builder()
            .id(announcement.getId())
            .title(announcement.getTitle())
            .department(announcement.getDepartment())
            .executionAgency(announcement.getExecutionAgency())
            .startDate(announcement.getStartDate())
            .endDate(announcement.getEndDate())
            .overview(announcement.getOverview())
            .applicationMethod(announcement.getApplicationMethod())
            .contact(announcement.getContact())
            .originalLink(announcement.getOriginalLink())
            .tag(announcement.getTag())
            .status(announcement.getStatus())
            .createdAt(announcement.getCreatedAt())
            .build();
    }

}
