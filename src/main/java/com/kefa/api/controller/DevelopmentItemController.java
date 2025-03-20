package com.kefa.api.controller;

import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemDeleteRequest;
import com.kefa.api.dto.developmentItem.command.DevelopmentItemUpdateCommand;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemUpdateRequest;
import com.kefa.api.dto.developmentItem.response.DevelopmentItemResponse;
import com.kefa.application.service.DevelopmentItemService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DevelopmentItemController {

    private final DevelopmentItemService developmentItemService;

    @DeleteMapping("/company/{companyId}/item/{itemId}")
    public ApiResponse<Void> delete(@PathVariable Long companyId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid DevelopmentItemDeleteRequest request,
                                    @AuthenticationPrincipal LoginAccount loginAccount) {

        developmentItemService.delete(companyId, itemId, loginAccount.getId());

        return ApiResponse.success();
    }

    @PutMapping("/company/{companyId}/item/{itemId}")
    public ApiResponse<DevelopmentItemResponse> update(@PathVariable Long companyId,
                                                       @PathVariable Long itemId,
                                                       @RequestBody @Valid DevelopmentItemUpdateRequest request,
                                                       @AuthenticationPrincipal LoginAccount loginAccount) {

        DevelopmentItemUpdateCommand command = DevelopmentItemUpdateCommand.builder()
            .companyId(companyId)
            .itemId(itemId)
            .request(request)
            .accountId(loginAccount.getId())
            .build();

        return ApiResponse.success(developmentItemService.update(command));
    }

    @PostMapping("/company/{companyId}/item")
    public ApiResponse<DevelopmentItemResponse> add(@PathVariable Long companyId, @RequestBody @Valid DevelopmentItemAddRequest request, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(developmentItemService.add(companyId, request, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/items")
    public ApiResponse<List<DevelopmentItemResponse>> getAll(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(developmentItemService.getAll(companyId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/item/{itemId}")
    public ApiResponse<DevelopmentItemResponse> get(@PathVariable Long companyId, @PathVariable Long itemId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(developmentItemService.get(companyId, itemId, loginAccount.getId()));
    }

}
