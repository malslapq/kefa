package com.kefa.api.controller;

import com.kefa.api.dto.document.command.ConsultingDocumentUploadCommand;
import com.kefa.api.dto.document.command.GetConsultingSessionDocsCommand;
import com.kefa.api.dto.document.request.UpdateDocumentNameRequest;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.service.ConsultingSessionDocumentService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DocumentController {

    private final ConsultingSessionDocumentService consultingSessionDocumentService;

    @PostMapping("/company/{companyId}/consulting/{consultingSessionId}/docs")
    public ApiResponse<Void> upload(@PathVariable Long companyId,
                                    @PathVariable Long consultingSessionId,
                                    @RequestParam List<MultipartFile> files,
                                    @AuthenticationPrincipal LoginAccount loginAccount) {

        ConsultingDocumentUploadCommand command = ConsultingDocumentUploadCommand.builder()
            .companyId(companyId)
            .consultingSessionId(consultingSessionId)
            .loginAccountId(loginAccount.getId())
            .files(files)
            .build();

        consultingSessionDocumentService.fileUpload(command);
        return ApiResponse.success();
    }


    @GetMapping("/company/{companyId}/consulting/{consultingSessionId}/docs")
    public ApiResponse<PagedResponse<SaveFileDto>> getDocs(@PathVariable Long companyId,
                                                           @PathVariable Long consultingSessionId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @AuthenticationPrincipal LoginAccount loginAccount) {

        GetConsultingSessionDocsCommand command = GetConsultingSessionDocsCommand.builder()
            .companyId(companyId)
            .consultingSessionId(consultingSessionId)
            .loginAccountId(loginAccount.getId())
            .page(page)
            .size(size)
            .build();

        return ApiResponse.success(consultingSessionDocumentService.getDocs(command));
    }

    @PatchMapping("/company/consulting/doc/{documentId}")
    public ApiResponse<Void> updateConsultingSessionDocsName(@PathVariable Long documentId,
                                                             @AuthenticationPrincipal LoginAccount loginAccount,
                                                             @RequestBody @Valid UpdateDocumentNameRequest request) {

        consultingSessionDocumentService.updateName(documentId, loginAccount.getId(), request);
        return ApiResponse.success();
    }

    @DeleteMapping("/company/consulting/doc/{documentId}")
    public ApiResponse<String> deleteConsultingSessionDoc(@PathVariable Long documentId, @AuthenticationPrincipal LoginAccount loginAccount) {
        consultingSessionDocumentService.delete(documentId, loginAccount.getId());
        return ApiResponse.success();
    }

}
