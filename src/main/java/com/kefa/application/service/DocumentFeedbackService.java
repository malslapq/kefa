package com.kefa.application.service;

import com.kefa.api.dto.document.command.AddDocumentFeedbackCommand;
import com.kefa.api.dto.document.command.UpdateDocumentFeedbackCommand;
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

    @Transactional(readOnly = true)
    public List<DocumentFeedbackResponse> getAll(Long consultingSessionId, Long documentId, LoginAccount loginAccount) {
        consultingSessionUseCase.validateAccountIsParticipant(consultingSessionId, loginAccount.getId());
        return documentFeedbackUseCase.getAll(documentId);
    }

    public DocumentFeedbackResponse add(AddDocumentFeedbackCommand command) {
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccount().getId());
        return documentFeedbackUseCase.add(command);
    }

    @Transactional
    public DocumentFeedbackResponse update(UpdateDocumentFeedbackCommand command) {
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccount().getId());
        return documentFeedbackUseCase.update(command);
    }

    public void delete(Long feedbackId, Long loginAccountId) {
        documentFeedbackUseCase.delete(feedbackId, loginAccountId);
    }
}
