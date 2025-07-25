package com.kefa.application.service;

import com.kefa.api.dto.document.command.DocumentFeedbackAddCommand;
import com.kefa.api.dto.document.command.DocumentFeedbackUpdateCommand;
import com.kefa.api.dto.document.response.DocumentFeedbackResponse;
import com.kefa.application.usecase.ConsultingSessionUseCase;
import com.kefa.application.usecase.DocumentFeedbackUseCase;
import com.kefa.infrastructure.security.auth.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentFeedbackService {

    private final DocumentFeedbackUseCase documentFeedbackUseCase;
    private final ConsultingSessionUseCase consultingSessionUseCase;

    /**
     * Retrieves all feedback entries for a specific document within a consulting session, after validating the user's participation.
     *
     * @param consultingSessionId the ID of the consulting session
     * @param documentId the ID of the document for which feedback is requested
     * @param loginAccount the account requesting the feedback, used for participant validation
     * @return a list of feedback responses associated with the specified document
     */
    @Transactional(readOnly = true)
    public List<DocumentFeedbackResponse> getAll(Long consultingSessionId, Long documentId, LoginAccount loginAccount) {
        consultingSessionUseCase.validateAccountIsParticipant(consultingSessionId, loginAccount.getId());
        return documentFeedbackUseCase.getAll(documentId);
    }

    /**
     * Adds new feedback to a document within a consulting session.
     *
     * Validates that the login account in the command is a participant in the specified consulting session before adding the feedback.
     *
     * @param command the command containing details for the feedback to be added
     * @return the response representing the newly created document feedback
     */
    @Transactional
    public DocumentFeedbackResponse add(DocumentFeedbackAddCommand command) {
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccount().getId());
        return documentFeedbackUseCase.add(command);
    }

    /**
     * Updates an existing document feedback entry within a consulting session.
     *
     * @param command the update command containing feedback details and participant information
     * @return the updated document feedback response
     */
    @Transactional
    public DocumentFeedbackResponse update(DocumentFeedbackUpdateCommand command) {
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccount().getId());
        return documentFeedbackUseCase.update(command);
    }

    public void delete(Long feedbackId, Long loginAccountId) {
        documentFeedbackUseCase.delete(feedbackId, loginAccountId);
    }
}
