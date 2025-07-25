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

    /**
     * Creates a new notice using the provided request data and authenticated user information.
     *
     * @param request the data required to create a new notice
     * @param loginAccount the authenticated user's account information
     * @return the response containing details of the created notice
     */
    public NoticeCreateResponseDto create(NoticeCreateRequestDto request, LoginAccount loginAccount) {
        return noticeUseCase.create(request, loginAccount);
    }

    /**
     * Updates an existing notice with the provided data and returns the update result.
     *
     * @param request   the data for updating the notice
     * @param noticeId  the identifier of the notice to update
     * @return          the response containing updated notice information
     */
    public NoticeUpdateResponseDto update(NoticeUpdateRequestDto request, Long noticeId, LoginAccount loginAccount) {
        return noticeUseCase.update(request, noticeId, loginAccount);
    }

    /**
     * Deletes a notice identified by its ID using the provided login account for authorization.
     *
     * @param noticeId the unique identifier of the notice to delete
     */
    public void delete(Long noticeId, LoginAccount loginAccount) {
        noticeUseCase.delete(noticeId, loginAccount);
    }

    /**
     * Retrieves the details of a notice by its unique identifier.
     *
     * @param noticeId the ID of the notice to retrieve
     * @return the notice details as a NoticeResponseDto
     */
    public NoticeResponseDto getNotice(Long noticeId) {
        return noticeUseCase.getNotice(noticeId);
    }

    /**
     * Retrieves a paginated list of notices based on the specified criteria.
     *
     * @param command the command object containing filtering and pagination parameters
     * @return a paged response containing notice data matching the criteria
     */
    public PagedResponse<NoticeResponseDto> getNotices(NoticesGetCommand command) {
        return noticeUseCase.getNotices(command);
    }
}
