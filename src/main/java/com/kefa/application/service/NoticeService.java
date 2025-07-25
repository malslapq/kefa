package com.kefa.application.service;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.notice.command.NoticesGetCommand;
import com.kefa.api.dto.notice.request.NoticeCreateRequestDto;
import com.kefa.api.dto.notice.request.NoticeUpdateRequestDto;
import com.kefa.api.dto.notice.response.NoticeCreateResponseDto;
import com.kefa.api.dto.notice.response.NoticeResponseDto;
import com.kefa.api.dto.notice.response.NoticeUpdateResponseDto;
import com.kefa.application.usecase.NoticeUseCase;
import com.kefa.infrastructure.security.auth.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeUseCase noticeUseCase;

    public NoticeCreateResponseDto create(NoticeCreateRequestDto request, LoginAccount loginAccount) {
        return noticeUseCase.create(request, loginAccount);
    }

    public NoticeUpdateResponseDto update(NoticeUpdateRequestDto request, Long noticeId, LoginAccount loginAccount) {
        return noticeUseCase.update(request, noticeId, loginAccount);
    }

    public void delete(Long noticeId, LoginAccount loginAccount) {
        noticeUseCase.delete(noticeId, loginAccount);
    }

    public NoticeResponseDto getNotice(Long noticeId) {
        return noticeUseCase.getNotice(noticeId);
    }

    public PagedResponse<NoticeResponseDto> getNotices(NoticesGetCommand command) {
        return noticeUseCase.getNotices(command);
    }
}
