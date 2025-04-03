package com.kefa.application.usecase;

import com.kefa.api.dto.document.command.AddDocumentFeedbackCommand;
import com.kefa.api.dto.document.command.UpdateDocumentFeedbackCommand;
import com.kefa.api.dto.document.response.DocumentFeedbackResponse;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.FeedbackException;
import com.kefa.domain.entity.DocumentFeedback;
import com.kefa.infrastructure.repository.DocumentFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentFeedbackUseCase {

    private final DocumentFeedbackRepository documentFeedbackRepository;

    public List<DocumentFeedbackResponse> getAll(Long documentId) {
        List<DocumentFeedback> feedbacks = documentFeedbackRepository.findAllByConsultingSessionDocumentId(documentId);
        return feedbacks.stream().map(DocumentFeedbackResponse::from).toList();
    }

    public DocumentFeedbackResponse add(AddDocumentFeedbackCommand command) {

        DocumentFeedback feedback = DocumentFeedback.builder()
            .name(command.getLoginAccount().getName())
            .content(command.getContent())
            .accountId(command.getLoginAccount().getId())
            .build();

        return DocumentFeedbackResponse.from(documentFeedbackRepository.save(feedback));
    }

    public DocumentFeedbackResponse update(UpdateDocumentFeedbackCommand command) {

        DocumentFeedback feedback = documentFeedbackRepository.findByIdWithDocument(command.getFeedbackId())
            .orElseThrow(()-> new FeedbackException(ErrorCode.NOT_FOUND_FEEDBACK));

        validateAuthor(feedback.getAccountId(), command.getLoginAccount().getId());
        validateDocumentId(feedback.getConsultingSessionDocument().getId(), command.getDocumentId());

        feedback.updateContent(command.getContent());

        return DocumentFeedbackResponse.from(documentFeedbackRepository.save(feedback));
    }

    public void delete(Long feedbackId, Long loginAccountId) {
        DocumentFeedback feedback = documentFeedbackRepository.findById(feedbackId).orElseThrow(() -> new FeedbackException(ErrorCode.NOT_FOUND_FEEDBACK));

        validateAuthor(feedback.getAccountId(), loginAccountId);

        documentFeedbackRepository.delete(feedback);
    }

    private void validateAuthor(Long getAccountId, Long loginAccountId){
        if(!getAccountId.equals(loginAccountId)){
            throw new FeedbackException(ErrorCode.FORBIDDEN_FEEDBACK_UPDATE);
        }
    }

    private void validateDocumentId(Long getDocumentId, Long inputDocumentId) {
        if(!getDocumentId.equals(inputDocumentId)) {
            throw new FeedbackException(ErrorCode.INVALID_DOCUMENT_ID);
        }
    }
}
