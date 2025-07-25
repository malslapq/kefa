package com.kefa.api.dto.notice.response;

import com.kefa.domain.entity.Notice;
import com.kefa.domain.vo.AccountVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeUpdateResponseDto {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int viewCount;
    private boolean isPinned;
    private LocalDateTime createdAt;

    public static NoticeUpdateResponseDto from(Notice notice) {
        return NoticeUpdateResponseDto.builder()
            .id(notice.getId())
            .title(notice.getTitle())
            .writer(AccountVO.from(notice.getAccount()).getName())
            .content(notice.getContent())
            .isPinned(notice.isPinned())
            .viewCount(notice.getViewCount())
            .createdAt(notice.getCreatedAt())
            .build();
    }
}
