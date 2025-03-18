package com.kefa.application.usecase;

import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import com.kefa.infrastructure.aws.s3.S3Service;
import com.kefa.infrastructure.repository.ConsultingDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingDocumentUseCase {

    private final S3Service s3Service;
    private final ConsultingDocumentRepository consultingDocumentRepository;

    public List<SaveFileDto> uploadFiles(List<MultipartFile> files) {
        return s3Service.uploadFile(files);
    }


    public void saveFilesUrl(List<SaveFileDto> saveFileDtos, Long loginAccountId) {
        List<ConsultingSessionDocument> consultingSessionDocuments =
            saveFileDtos.stream().map(dto -> ConsultingSessionDocument.of(dto, loginAccountId)).toList();
        consultingDocumentRepository.saveAll(consultingSessionDocuments);
    }
}
