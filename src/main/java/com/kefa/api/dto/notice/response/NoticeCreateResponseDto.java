package com.kefa.api.dto.notice.response;

import com.kefa.domain.entity.Notice;
import com.kefa.domain.vo.AccountVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeCreateResponseDto {

    private Long id;
    private String title;
    private String content;
    private AccountVO account;
    private int viewCount;
    private boolean isPinned;

    /**
     * Creates a NoticeCreateResponseDto from a Notice entity by mapping its fields.
     *
     * @param notice the Notice entity to convert
     * @return a NoticeCreateResponseDto representing the given Notice
     */
    public static NoticeCreateResponseDto from(Notice notice) {
        return NoticeCreateResponseDto.builder()
            .id(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .viewCount(notice.getViewCount())
            .isPinned(notice.isPinned())
            .account(AccountVO.from(notice.getAccount()))
            .build();
    }

}
