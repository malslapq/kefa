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

    @PostMapping("/notice")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<NoticeCreateResponseDto> create(@RequestBody @Valid NoticeCreateRequestDto request, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(noticeService.create(request, loginAccount));
    }

    @PutMapping("/notice/{noticeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<NoticeUpdateResponseDto> update(@RequestBody @Valid NoticeUpdateRequestDto request, @AuthenticationPrincipal LoginAccount loginAccount, @PathVariable Long noticeId) {
        return ApiResponse.success(noticeService.update(request, noticeId, loginAccount));
    }

    @DeleteMapping("/notice/{noticeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Void> delete(@AuthenticationPrincipal LoginAccount loginAccount, @PathVariable Long noticeId) {
        noticeService.delete(noticeId, loginAccount);
        return ApiResponse.success();
    }

    @GetMapping("/notice/{noticeId}")
    public ApiResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ApiResponse.success(noticeService.getNotice(noticeId));
    }

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
