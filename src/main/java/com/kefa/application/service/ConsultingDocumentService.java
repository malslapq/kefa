package com.kefa.application.service;

import com.kefa.api.dto.consulting.command.ConsultingDocumentUploadCommand;
import com.kefa.api.dto.consulting.command.GetConsultingSessionDocsCommand;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.application.usecase.ConsultingDocumentUseCase;
import com.kefa.application.usecase.ConsultingSessionUseCase;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingDocumentService {

    private final ConsultingDocumentUseCase consultingDocumentUseCase;
    private final ConsultingSessionUseCase consultingSessionUseCase;
    private final CompanyUseCase companyUseCase;

    @Transactional
    public void fileUpload(ConsultingDocumentUploadCommand command) {

        companyUseCase.validateCompanyOwnershipAndProcess(command.getCompanyId(), command.getLoginAccountId());
        consultingSessionUseCase.validateUserIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        List<SaveFileDto> uploadedFilesUrl = consultingDocumentUseCase.uploadFiles(command.getFiles());

        consultingDocumentUseCase.saveFilesUrl(uploadedFilesUrl, command.getLoginAccountId());

    }

    public PagedResponse<SaveFileDto> getDocs(GetConsultingSessionDocsCommand command) {



        return null;
    }

}
