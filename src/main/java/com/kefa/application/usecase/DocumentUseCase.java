package com.kefa.application.usecase;

import com.kefa.api.dto.document.request.DocumentUpdateNameRequest;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.common.exception.DocumentException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import com.kefa.infrastructure.aws.s3.S3Service;
import com.kefa.infrastructure.repository.DocumentRepository;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentUseCase {

    private final S3Service s3Service;
    private final DocumentRepository documentRepository;
    private final ConsultingSessionRepository consultingSessionRepository;

    public List<SaveFileDto> uploadFiles(List<MultipartFile> files) {
        return s3Service.uploadFile(files);
    }

    public void deleteFile(String fileUrl) {
        s3Service.deleteFile(fileUrl);
    }

    public void saveFilesUrl(List<SaveFileDto> saveFileDtos, Long consultingSessionId, Long loginAccountId) {

        ConsultingSession consultingSession = consultingSessionRepository.findById(consultingSessionId)
            .orElseThrow(() -> new DocumentException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        List<ConsultingSessionDocument> consultingSessionDocuments =
            saveFileDtos.stream()
                .map(dto -> ConsultingSessionDocument.of(dto, loginAccountId))
                .toList();

        documentRepository.saveAll(consultingSessionDocuments);
        consultingSession.addDocuments(consultingSessionDocuments);
    }

    /**
     * Retrieves a paginated list of documents for a given consulting session.
     *
     * @param consultingSessionId the ID of the consulting session whose documents are to be retrieved
     * @param page the zero-based page index to retrieve
     * @param size the number of documents per page
     * @return a paged response containing document metadata and pagination details
     */
    public PagedResponse<SaveFileDto> getDocs(Long consultingSessionId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ConsultingSessionDocument> documentPage = documentRepository.findByConsultingSessionId(consultingSessionId, pageRequest);

        return PagedResponse.<SaveFileDto>builder()
            .content(documentPage.map(SaveFileDto::from).getContent())
            .page(documentPage.getNumber())
            .size(documentPage.getSize())
            .totalElements(documentPage.getTotalElements())
            .totalPages(documentPage.getTotalPages())
            .build();
    }

    /**
     * Updates the name of a consulting session document if the requesting account is the owner.
     *
     * @param documentId the ID of the document to update
     * @param loginAccountId the ID of the account requesting the update
     * @param request the request containing the new document name
     * @throws DocumentException if the document is not found or the account is not authorized to edit it
     */
    @Transactional
    public void updateName(Long documentId, Long loginAccountId, DocumentUpdateNameRequest request) {

        ConsultingSessionDocument document = getConsultingSessionDocumentById(documentId);

        validateDocumentOwnership(document.getSaveAccountId(), loginAccountId);

        document.updateName(request.getName());

    }

    public SaveFileDto delete(Long documentId, Long loginAccountId) {

        ConsultingSessionDocument document = getConsultingSessionDocumentById(documentId);

        validateDocumentOwnership(document.getSaveAccountId(), loginAccountId);

        documentRepository.deleteById(documentId);

        return SaveFileDto.from(document);
    }

    private void validateDocumentOwnership(Long saveDocumentAccountId, Long loginAccountId) {
        if (!saveDocumentAccountId.equals(loginAccountId)) {
            throw new DocumentException(ErrorCode.UNAUTHORIZED_DOCUMENT_EDIT);
        }
    }

    private ConsultingSessionDocument getConsultingSessionDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentException(ErrorCode.NOT_FOUND_DOCUMENT));
    }
}
