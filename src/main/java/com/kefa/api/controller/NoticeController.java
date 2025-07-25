package com.kefa.api.controller;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.notice.command.NoticesGetCommand;
import com.kefa.api.dto.notice.request.NoticeCreateRequestDto;
import com.kefa.api.dto.notice.request.NoticeUpdateRequestDto;
import com.kefa.api.dto.notice.response.NoticeCreateResponseDto;
import com.kefa.api.dto.notice.response.NoticeResponseDto;
import com.kefa.api.dto.notice.response.NoticeUpdateResponseDto;
import com.kefa.application.service.NoticeService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * Creates a new notice using the provided request data.
     *
     * Requires the user to have the 'ADMIN' role. Returns a success response containing the details of the created notice.
     *
     * @param request      the notice creation request data
     * @param loginAccount the authenticated user's account information
     * @return a success response with the created notice details
     */
    @PostMapping("/notice")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<NoticeCreateResponseDto> create(@RequestBody @Valid NoticeCreateRequestDto request, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(noticeService.create(request, loginAccount));
    }

    /**
     * Updates an existing notice identified by its ID.
     *
     * Requires the user to have the 'ADMIN' role. Accepts a validated notice update request and the authenticated user's account information.
     *
     * @param request   the notice update request data
     * @param noticeId  the ID of the notice to update
     * @return a success response containing the updated notice information
     */
    @PutMapping("/notice/{noticeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<NoticeUpdateResponseDto> update(@RequestBody @Valid NoticeUpdateRequestDto request, @AuthenticationPrincipal LoginAccount loginAccount, @PathVariable Long noticeId) {
        return ApiResponse.success(noticeService.update(request, noticeId, loginAccount));
    }

    /**
     * Deletes a notice identified by its ID. Restricted to users with the 'ADMIN' role.
     *
     * @param noticeId the ID of the notice to delete
     * @return a success response with no content
     */
    @DeleteMapping("/notice/{noticeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Void> delete(@AuthenticationPrincipal LoginAccount loginAccount, @PathVariable Long noticeId) {
        noticeService.delete(noticeId, loginAccount);
        return ApiResponse.success();
    }

    /**
     * Retrieves the details of a specific notice by its ID.
     *
     * @param noticeId the unique identifier of the notice to retrieve
     * @return a success response containing the notice details
     */
    @GetMapping("/notice/{noticeId}")
    public ApiResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ApiResponse.success(noticeService.getNotice(noticeId));
    }

    /**
     * Retrieves a paginated list of notices, optionally filtered by keyword and search type.
     *
     * @param keyword     an optional keyword to filter notices by content or title
     * @param searchType  an optional type specifying the field to search (e.g., title, content)
     * @param page        the page number to retrieve (zero-based, defaults to 0)
     * @param size        the number of notices per page (defaults to 10)
     * @return a success response containing a paged list of notice details
     */
    @GetMapping("/notices")
    public ApiResponse<PagedResponse<NoticeResponseDto>> getNotices(@RequestParam(required = false) String keyword,
                                                                    @RequestParam(required = false) String searchType,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {

        NoticesGetCommand command = NoticesGetCommand.builder()
            .keyword(keyword)
            .searchType(searchType)
            .page(page)
            .size(size)
            .build();

        return ApiResponse.success(noticeService.getNotices(command));
    }
}
