package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.notice.command.NoticesGetCommand;
import com.kefa.api.dto.notice.request.NoticeCreateRequestDto;
import com.kefa.api.dto.notice.request.NoticeUpdateRequestDto;
import com.kefa.api.dto.notice.response.NoticeCreateResponseDto;
import com.kefa.api.dto.notice.response.NoticeResponseDto;
import com.kefa.api.dto.notice.response.NoticeUpdateResponseDto;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NoticeException;
import com.kefa.common.type.NoticesSearchType;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Notice;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.NoticeRepository;
import com.kefa.infrastructure.security.auth.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NoticeUseCase {

    private final NoticeRepository noticeRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public NoticeCreateResponseDto create(NoticeCreateRequestDto requestDto, LoginAccount loginAccount) {

        Account account = accountRepository.findById(loginAccount.getId()).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_ACCOUNT));
        Notice notice = noticeRepository.save(Notice.of(requestDto, account));

        return NoticeCreateResponseDto.from(notice);
    }

    @Transactional
    public NoticeUpdateResponseDto update(NoticeUpdateRequestDto request, Long noticeId, LoginAccount loginAccount) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));
        validateWriter(loginAccount.getId(), notice);
        notice.update(request.getTitle(), request.getContent(), request.isPinned());
        return NoticeUpdateResponseDto.from(notice);
    }

    @Transactional
    public void delete(Long noticeId, LoginAccount loginAccount) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));
        validateWriter(loginAccount.getId(), notice);
        noticeRepository.delete(notice);

    }

    @Transactional(readOnly = true)
    public NoticeResponseDto getNotice(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));

        return NoticeResponseDto.from(notice);
    }

    @Transactional(readOnly = true)
    public PagedResponse<NoticeResponseDto> getNotices(NoticesGetCommand command) {

        Pageable pageable = PageRequest.of(
            command.getPage(),
            command.getSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return getNoticesFromSearchType(command.getKeyword(), command.getSearchType(), pageable);
    }

    private PagedResponse<NoticeResponseDto> getNoticesFromSearchType(String keyword, String searchType, Pageable pageable) {

        NoticesSearchType noticesSearchType = NoticesSearchType.from(searchType);

        Page<Notice> notices;
        if (!StringUtils.hasText(keyword) || noticesSearchType == null) {
            notices = noticeRepository.findAll(pageable);
        } else {
            notices = switch (noticesSearchType) {
                case TITLE -> noticeRepository.findByTitleContaining(keyword, pageable);
                case CONTENT -> noticeRepository.findByContentContaining(keyword, pageable);
                case WRITER -> noticeRepository.findByAccount_NameContaining(keyword, pageable);
                case TOTAL -> noticeRepository.findAll(pageable);
            };
        }

        return PagedResponse.<NoticeResponseDto>builder()
            .content(notices.map(NoticeResponseDto::from).getContent())
            .page(notices.getNumber())
            .size(notices.getSize())
            .totalElements(notices.getTotalElements())
            .totalPages(notices.getTotalPages())
            .build();
    }

    private void validateWriter(Long loginAccountId, Notice notice) {
        if (!notice.getAccount().getId().equals(loginAccountId)) {
            throw new NoticeException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
