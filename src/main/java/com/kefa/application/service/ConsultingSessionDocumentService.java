package com.kefa.application.service;

import com.kefa.api.dto.consulting.command.ConsultingDocumentUploadCommand;
import com.kefa.api.dto.consulting.command.GetConsultingSessionDocsCommand;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.application.usecase.ConsultingSessionDocumentUseCase;
import com.kefa.application.usecase.ConsultingSessionUseCase;
import com.kefa.domain.entity.ConsultingSessionDocument;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        consultingSessionUseCase.validateUserIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        List<SaveFileDto> uploadedFilesUrl = consultingSessionDocumentUseCase.uploadFiles(command.getFiles());

        consultingSessionDocumentUseCase.saveFilesUrl(uploadedFilesUrl, command.getConsultingSessionId(), command.getLoginAccountId());

    }

    @Transactional(readOnly = true)
    public PagedResponse<SaveFileDto> getDocs(GetConsultingSessionDocsCommand command) {

        companyUseCase.validateCompanyOwnershipAndProcess(command.getCompanyId(), command.getLoginAccountId());
        consultingSessionUseCase.validateUserIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        return consultingSessionDocumentUseCase.getDocs(command.getConsultingSessionId(), PageRequest.of(command.getPage(), command.getSize()));
    }

}
