package com.kefa.api.controller;

import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
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

    @PostMapping("/company/{companyId}/item")
    public ApiResponse<DevelopmentItemResponse> add(@PathVariable String companyId, @RequestBody @Valid DevelopmentItemAddRequest request, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(developmentItemService.add(Long.valueOf(companyId), request, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/items")
    public ApiResponse<List<DevelopmentItemResponse>> getAll(@PathVariable String companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(developmentItemService.getAll(Long.valueOf(companyId), loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/item/{itemId}")
    public ApiResponse<DevelopmentItemResponse> get(@PathVariable String companyId, @PathVariable String itemId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(developmentItemService.get(Long.valueOf(companyId), Long.valueOf(itemId),loginAccount.getId()));
    }

}
