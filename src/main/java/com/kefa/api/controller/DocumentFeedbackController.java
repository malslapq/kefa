package com.kefa.api.controller;

import com.kefa.api.dto.document.command.DocumentFeedbackAddCommand;
import com.kefa.api.dto.document.command.DocumentFeedbackUpdateCommand;
import com.kefa.api.dto.document.request.DocumentFeedbackAddRequest;
import com.kefa.api.dto.document.request.DocumentFeedbackUpdateRequest;
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

    /**
     * Retrieves all feedback entries for a specific document within a consulting session.
     *
     * @param consultingSessionId the ID of the consulting session
     * @param documentId the ID of the document
     * @return a successful API response containing a list of document feedback responses
     */
    @GetMapping("/company/consulting/{consultingSessionId}/doc/{documentId}")
    public ApiResponse<List<DocumentFeedbackResponse>> getAll(@PathVariable Long consultingSessionId, @PathVariable Long documentId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(documentFeedbackService.getAll(consultingSessionId, documentId, loginAccount));
    }

    /**
     * Adds new feedback to a document within a specified consulting session.
     *
     * @param request the feedback details to add
     * @param consultingSessionId the ID of the consulting session
     * @param documentId the ID of the document to which feedback is added
     * @return the created feedback wrapped in an ApiResponse
     */
    @PostMapping("/company/consulting/{consultingSessionId}/doc/{documentId}")
    public ApiResponse<DocumentFeedbackResponse> add(@RequestBody @Valid DocumentFeedbackAddRequest request,
                                                     @PathVariable Long consultingSessionId,
                                                     @PathVariable Long documentId,
                                                     @AuthenticationPrincipal LoginAccount loginAccount) {

        DocumentFeedbackAddCommand command = DocumentFeedbackAddCommand.builder()
            .content(request.getContent())
            .consultingSessionId(consultingSessionId)
            .documentId(documentId)
            .loginAccount(loginAccount)
            .build();

        return ApiResponse.success(documentFeedbackService.add(command));
    }

    /**
     * Updates an existing document feedback entry within a consulting session.
     *
     * @param request the request containing updated feedback content
     * @param consultingSessionId the ID of the consulting session
     * @param documentId the ID of the document
     * @param feedbackId the ID of the feedback to update
     * @param loginAccount the authenticated user's account information
     * @return the updated feedback wrapped in an ApiResponse
     */
    @PutMapping("/company/consulting/{consultingSessionId}/doc/{documentId}/feedback/{feedbackId}")
    public ApiResponse<DocumentFeedbackResponse> update(@RequestBody @Valid DocumentFeedbackUpdateRequest request,
                                                        @PathVariable Long consultingSessionId,
                                                        @PathVariable Long documentId,
                                                        @PathVariable Long feedbackId,
                                                        @AuthenticationPrincipal LoginAccount loginAccount) {

        DocumentFeedbackUpdateCommand command = DocumentFeedbackUpdateCommand.builder()
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
