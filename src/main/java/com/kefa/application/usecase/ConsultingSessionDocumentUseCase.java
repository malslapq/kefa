package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.common.exception.ConsultingSessionDocumentException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import com.kefa.infrastructure.aws.s3.S3Service;
import com.kefa.infrastructure.repository.ConsultingDocumentRepository;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultingSessionDocumentUseCase {

    private final S3Service s3Service;
    private final ConsultingDocumentRepository consultingDocumentRepository;
    private final ConsultingSessionRepository consultingSessionRepository;

    public List<SaveFileDto> uploadFiles(List<MultipartFile> files) {
        return s3Service.uploadFile(files);
    }


    public void saveFilesUrl(List<SaveFileDto> saveFileDtos, Long consultingSessionId, Long loginAccountId) {

        ConsultingSession consultingSession = consultingSessionRepository.findById(consultingSessionId)
            .orElseThrow(() -> new ConsultingSessionDocumentException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        List<ConsultingSessionDocument> consultingSessionDocuments =
            saveFileDtos.stream()
                .map(dto -> {
                    ConsultingSessionDocument consultingSessionDocument = ConsultingSessionDocument.of(dto, loginAccountId);
                    consultingSessionDocument.addConsultingSession(consultingSession);
                    return consultingSessionDocument;
                })
                .toList();

        consultingDocumentRepository.saveAll(consultingSessionDocuments);
    }

    public PagedResponse<SaveFileDto> getDocs(Long consultingSessionId, PageRequest pageRequest) {

        Page<ConsultingSessionDocument> documentPage = consultingDocumentRepository.findByConsultingSessionId(consultingSessionId, pageRequest);

        return PagedResponse.<SaveFileDto>builder()
            .content(documentPage.map(SaveFileDto::from).getContent())
            .page(documentPage.getNumber())
            .size(documentPage.getSize())
            .totalElements(documentPage.getTotalElements())
            .totalPages(documentPage.getTotalPages())
            .build();
    }
}
