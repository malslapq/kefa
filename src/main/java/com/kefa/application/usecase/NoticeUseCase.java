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

    /**
     * Creates a new notice associated with the specified logged-in account.
     *
     * Retrieves the account by ID, throws a {@code NoticeException} if not found, and saves a new notice using the provided request data.
     *
     * @param requestDto the data for creating the notice
     * @param loginAccount the currently logged-in user's account information
     * @return a response DTO representing the created notice
     * @throws NoticeException if the account is not found
     */
    @Transactional
    public NoticeCreateResponseDto create(NoticeCreateRequestDto requestDto, LoginAccount loginAccount) {

        Account account = accountRepository.findById(loginAccount.getId()).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_ACCOUNT));
        Notice notice = noticeRepository.save(Notice.of(requestDto, account));

        return NoticeCreateResponseDto.from(notice);
    }

    /**
     * Updates an existing notice with new title, content, and pinned status after validating the writer's identity.
     *
     * @param request   the data containing updated notice information
     * @param noticeId  the ID of the notice to update
     * @param loginAccount the currently logged-in user's account information
     * @return a response DTO representing the updated notice
     * @throws NoticeException if the notice is not found or the user is not the writer
     */
    @Transactional
    public NoticeUpdateResponseDto update(NoticeUpdateRequestDto request, Long noticeId, LoginAccount loginAccount) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));
        validateWriter(loginAccount.getId(), notice);
        notice.update(request.getTitle(), request.getContent(), request.isPinned());
        return NoticeUpdateResponseDto.from(notice);
    }

    /**
     * Deletes a notice by its ID after verifying that the logged-in user is the author.
     *
     * @param noticeId the ID of the notice to delete
     * @param loginAccount the currently logged-in user's account information
     * @throws NoticeException if the notice does not exist or the user is not the author
     */
    @Transactional
    public void delete(Long noticeId, LoginAccount loginAccount) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));
        validateWriter(loginAccount.getId(), notice);
        noticeRepository.delete(notice);

    }

    /**
     * Retrieves a notice by its ID.
     *
     * @param noticeId the unique identifier of the notice to retrieve
     * @return a response DTO containing the notice details
     * @throws NoticeException if the notice is not found
     */
    @Transactional(readOnly = true)
    public NoticeResponseDto getNotice(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));

        return NoticeResponseDto.from(notice);
    }

    /**
     * Retrieves a paginated list of notices based on the provided search command.
     *
     * Applies optional keyword-based filtering and sorting by creation date in descending order.
     *
     * @param command the command containing pagination and search criteria
     * @return a paged response containing notice data matching the criteria
     */
    @Transactional(readOnly = true)
    public PagedResponse<NoticeResponseDto> getNotices(NoticesGetCommand command) {

        Pageable pageable = PageRequest.of(
            command.getPage(),
            command.getSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return getNoticesFromSearchType(command.getKeyword(), command.getSearchType(), pageable);
    }

    /**
     * Retrieves a paginated list of notices filtered by the specified search type and keyword.
     *
     * If the keyword is empty or the search type is invalid, returns all notices. Otherwise, filters notices by title, content, or writer name based on the search type.
     *
     * @param keyword     the search keyword to filter notices
     * @param searchType  the type of field to search by ("TITLE", "CONTENT", "WRITER", or "TOTAL")
     * @param pageable    pagination and sorting information
     * @return a paged response containing notice DTOs matching the search criteria
     */
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

    /**
     * Ensures that the given account ID matches the writer of the specified notice.
     *
     * @param loginAccountId the ID of the currently logged-in account
     * @param notice the notice to validate ownership for
     * @throws NoticeException if the account is not the writer of the notice
     */
    private void validateWriter(Long loginAccountId, Notice notice) {
        if (!notice.getAccount().getId().equals(loginAccountId)) {
            throw new NoticeException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
