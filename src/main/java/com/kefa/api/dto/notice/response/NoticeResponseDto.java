package com.kefa.api.dto.notice.response;

import com.kefa.domain.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int viewCount;
    private boolean isPinned;
    private LocalDateTime createdAt;

    /**
     * Creates a NoticeResponseDto from a Notice entity by mapping its properties.
     *
     * @param notice the Notice entity to convert
     * @return a NoticeResponseDto representing the given Notice
     */
    public static NoticeResponseDto from(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .writer(notice.getAccount().getName())
                .viewCount(notice.getViewCount())
                .isPinned(notice.isPinned())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
