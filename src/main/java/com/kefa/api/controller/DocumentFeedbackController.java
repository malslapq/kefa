package com.kefa.api.controller;

import com.kefa.api.dto.document.command.AddDocumentFeedbackCommand;
import com.kefa.api.dto.document.command.UpdateDocumentFeedbackCommand;
import com.kefa.api.dto.document.request.AddDocumentFeedbackRequest;
import com.kefa.api.dto.document.request.UpdateDocumentFeedbackRequest;
import com.kefa.api.dto.document.response.DocumentFeedbackResponse;
import com.kefa.application.service.DocumentFeedbackService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentFeedbackController {

    private final DocumentFeedbackService documentFeedbackService;

    @GetMapping("/company/consulting/{consultingSessionId}/doc/{documentId}")
    public ApiResponse<List<DocumentFeedbackResponse>> getAll(@PathVariable Long consultingSessionId, @PathVariable Long documentId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(documentFeedbackService.getAll(consultingSessionId, documentId, loginAccount));
    }

    @PostMapping("/company/consulting/{consultingSessionId}/doc/{documentId}")
    public ApiResponse<DocumentFeedbackResponse> add(@RequestBody @Valid AddDocumentFeedbackRequest request,
                                                     @PathVariable Long consultingSessionId,
                                                     @PathVariable Long documentId,
                                                     @AuthenticationPrincipal LoginAccount loginAccount) {

        AddDocumentFeedbackCommand command = AddDocumentFeedbackCommand.builder()
            .content(request.getContent())
            .consultingSessionId(consultingSessionId)
            .documentId(documentId)
            .loginAccount(loginAccount)
            .build();

        return ApiResponse.success(documentFeedbackService.add(command));
    }

    @PutMapping("/company/consulting/{consultingSessionId}/doc/{documentId}/feedback/{feedbackId}")
    public ApiResponse<DocumentFeedbackResponse> update(@RequestBody @Valid UpdateDocumentFeedbackRequest request,
                                                        @PathVariable Long consultingSessionId,
                                                        @PathVariable Long documentId,
                                                        @PathVariable Long feedbackId,
                                                        @AuthenticationPrincipal LoginAccount loginAccount) {

        UpdateDocumentFeedbackCommand command = UpdateDocumentFeedbackCommand.builder()
            .content(request.getContent())
            .consultingSessionId(consultingSessionId)
            .documentId(documentId)
            .feedbackId(feedbackId)
            .loginAccount(loginAccount)
            .build();

        return ApiResponse.success(documentFeedbackService.update(command));
    }

    @DeleteMapping("/company/consulting/doc/feedback/{feedbackId}")
    public ApiResponse<String> delete(@PathVariable Long feedbackId, @AuthenticationPrincipal LoginAccount loginAccount){

        documentFeedbackService.delete(feedbackId, loginAccount.getId());

        return ApiResponse.success("삭제 성공");
    }

}
