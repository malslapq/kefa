package com.kefa.application.usecase;

import com.kefa.api.dto.document.command.DocumentFeedbackAddCommand;
import com.kefa.api.dto.document.command.DocumentFeedbackUpdateCommand;
import com.kefa.api.dto.document.response.DocumentFeedbackResponse;
import com.kefa.common.exception.DocumentException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.FeedbackException;
import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.domain.entity.DocumentFeedback;
import com.kefa.infrastructure.repository.DocumentRepository;
import com.kefa.infrastructure.repository.DocumentFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentFeedbackUseCase {

    private final DocumentFeedbackRepository documentFeedbackRepository;
    private final DocumentRepository documentRepository;

    /**
     * Retrieves all feedback responses associated with a specific document ID.
     *
     * @param documentId the ID of the document for which feedback is requested
     * @return a list of feedback response DTOs for the specified document
     */
    public List<DocumentFeedbackResponse> getAll(Long documentId) {
        List<DocumentFeedback> feedbacks = documentFeedbackRepository.findAllByConsultingSessionDocumentId(documentId);
        return feedbacks.stream().map(DocumentFeedbackResponse::from).toList();
    }

    /**
     * Adds new feedback to a specified document.
     *
     * Creates a feedback entry using the provided command, associates it with the target document, saves both the document and feedback, and returns the created feedback as a response DTO.
     *
     * @param command the command containing feedback content, author information, and target document ID
     * @return the response DTO representing the newly added feedback
     * @throws DocumentException if the specified document does not exist
     */
    public DocumentFeedbackResponse add(DocumentFeedbackAddCommand command) {

        ConsultingSessionDocument document = documentRepository.findById(command.getDocumentId()).orElseThrow(() -> new DocumentException(ErrorCode.NOT_FOUND_DOCUMENT));

        DocumentFeedback feedback = DocumentFeedback.builder()
            .name(command.getLoginAccount().getName())
            .content(command.getContent())
            .accountId(command.getLoginAccount().getId())
            .consultingSessionDocument(document)
            .build();

        document.addFeedback(feedback);
        documentRepository.save(document);

        return DocumentFeedbackResponse.from(documentFeedbackRepository.save(feedback));
    }

    /**
     * Updates the content of an existing document feedback entry.
     *
     * Validates that the requesting account is the author of the feedback and that the feedback belongs to the specified document.
     * Throws a {@code FeedbackException} if the feedback is not found, the author does not match, or the document ID is invalid.
     *
     * @param command the command containing feedback update details
     * @return the updated feedback as a response DTO
     */
    public DocumentFeedbackResponse update(DocumentFeedbackUpdateCommand command) {

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
