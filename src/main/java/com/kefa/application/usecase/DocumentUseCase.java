package com.kefa.application.usecase;

import com.kefa.api.dto.document.request.UpdateDocumentNameRequest;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.common.exception.DocumentException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import com.kefa.infrastructure.aws.s3.S3Service;
import com.kefa.infrastructure.repository.ConsultingSessionDocumentRepository;
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
    private final ConsultingSessionDocumentRepository consultingSessionDocumentRepository;
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
                .map(dto -> {
                    ConsultingSessionDocument consultingSessionDocument = ConsultingSessionDocument.of(dto, loginAccountId);
                    consultingSessionDocument.addConsultingSession(consultingSession);
                    return consultingSessionDocument;
                })
                .toList();

        consultingSessionDocumentRepository.saveAll(consultingSessionDocuments);
        consultingSession.addDocuments(consultingSessionDocuments);
    }

    public PagedResponse<SaveFileDto> getDocs(Long consultingSessionId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ConsultingSessionDocument> documentPage = consultingSessionDocumentRepository.findByConsultingSessionId(consultingSessionId, pageRequest);

        return PagedResponse.<SaveFileDto>builder()
            .content(documentPage.map(SaveFileDto::from).getContent())
            .page(documentPage.getNumber())
            .size(documentPage.getSize())
            .totalElements(documentPage.getTotalElements())
            .totalPages(documentPage.getTotalPages())
            .build();
    }

    @Transactional
    public void updateName(Long documentId, Long loginAccountId, UpdateDocumentNameRequest request) {

        ConsultingSessionDocument document = getConsultingSessionDocumentById(documentId);

        validateDocumentOwnership(document.getSaveAccountId(), loginAccountId);

        document.updateName(request.getName());

    }

    public SaveFileDto delete(Long documentId, Long loginAccountId) {

        ConsultingSessionDocument document = getConsultingSessionDocumentById(documentId);

        validateDocumentOwnership(document.getSaveAccountId(), loginAccountId);

        consultingSessionDocumentRepository.deleteById(documentId);

        return SaveFileDto.from(document);
    }

    private void validateDocumentOwnership(Long saveDocumentAccountId, Long loginAccountId) {
        if (!saveDocumentAccountId.equals(loginAccountId)) {
            throw new DocumentException(ErrorCode.UNAUTHORIZED_DOCUMENT_EDIT);
        }
    }

    private ConsultingSessionDocument getConsultingSessionDocumentById(Long documentId) {
        return consultingSessionDocumentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentException(ErrorCode.NOT_FOUND_DOCUMENT));
    }
}
