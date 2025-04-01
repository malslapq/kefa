package com.kefa.application.service;

import com.kefa.api.dto.document.command.ConsultingDocumentUploadCommand;
import com.kefa.api.dto.document.command.GetConsultingSessionDocsCommand;
import com.kefa.api.dto.consulting.request.UpdateDocNameRequest;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.application.usecase.ConsultingSessionDocumentUseCase;
import com.kefa.application.usecase.ConsultingSessionUseCase;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingSessionDocumentService {

    private final ConsultingSessionDocumentUseCase consultingSessionDocumentUseCase;
    private final ConsultingSessionUseCase consultingSessionUseCase;
    private final CompanyUseCase companyUseCase;

    @Transactional
    public void fileUpload(ConsultingDocumentUploadCommand command) {

        companyUseCase.validateCompanyOwnershipAndProcess(command.getCompanyId(), command.getLoginAccountId());
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        List<SaveFileDto> uploadedFilesUrl = consultingSessionDocumentUseCase.uploadFiles(command.getFiles());

        consultingSessionDocumentUseCase.saveFilesUrl(uploadedFilesUrl, command.getConsultingSessionId(), command.getLoginAccountId());

    }

    @Transactional(readOnly = true)
    public PagedResponse<SaveFileDto> getDocs(GetConsultingSessionDocsCommand command) {

        companyUseCase.validateCompanyOwnershipAndProcess(command.getCompanyId(), command.getLoginAccountId());
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        return consultingSessionDocumentUseCase.getDocs(command.getConsultingSessionId(), command.getPage(), command.getSize());
    }

    public void updateName(Long documentId, Long loginAccountId, UpdateDocNameRequest request) {
        consultingSessionDocumentUseCase.updateName(documentId, loginAccountId, request);
    }

    @Transactional
    public void delete(Long documentId, Long loginAccountId) {
        SaveFileDto saveFileDto = consultingSessionDocumentUseCase.delete(documentId, loginAccountId);
        consultingSessionDocumentUseCase.deleteFile(saveFileDto.getUrl());
    }
}
