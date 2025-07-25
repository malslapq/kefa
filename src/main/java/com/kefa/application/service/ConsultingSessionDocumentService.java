package com.kefa.application.service;

import com.kefa.api.dto.document.command.ConsultingDocumentUploadCommand;
import com.kefa.api.dto.document.command.ConsultingSessionDocsGetCommand;
import com.kefa.api.dto.document.request.DocumentUpdateNameRequest;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.application.usecase.DocumentUseCase;
import com.kefa.application.usecase.ConsultingSessionUseCase;
import com.kefa.infrastructure.aws.dto.SaveFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingSessionDocumentService {

    private final DocumentUseCase documentUseCase;
    private final ConsultingSessionUseCase consultingSessionUseCase;
    private final CompanyUseCase companyUseCase;

    /**
     * Uploads files for a consulting session after validating company ownership and user participation.
     *
     * Associates the uploaded files with the specified consulting session and user.
     *
     * @param command the command containing file data, consulting session ID, company ID, and user ID
     */
    @Transactional
    public void fileUpload(ConsultingDocumentUploadCommand command) {

        companyUseCase.validateCompanyOwnershipAndProcess(command.getCompanyId(), command.getLoginAccountId());
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        List<SaveFileDto> uploadedFilesUrl = documentUseCase.uploadFiles(command.getFiles());

        documentUseCase.saveFilesUrl(uploadedFilesUrl, command.getConsultingSessionId(), command.getLoginAccountId());

    }

    /**
     * Retrieves a paginated list of documents associated with a consulting session after validating company ownership and user participation.
     *
     * @param command the command containing consulting session ID, company ID, login account ID, and pagination parameters
     * @return a paged response containing document information for the specified consulting session
     */
    @Transactional(readOnly = true)
    public PagedResponse<SaveFileDto> getDocs(ConsultingSessionDocsGetCommand command) {

        companyUseCase.validateCompanyOwnershipAndProcess(command.getCompanyId(), command.getLoginAccountId());
        consultingSessionUseCase.validateAccountIsParticipant(command.getConsultingSessionId(), command.getLoginAccountId());

        return documentUseCase.getDocs(command.getConsultingSessionId(), command.getPage(), command.getSize());
    }

    /****
     * Updates the name of a document with the specified ID for the given user.
     *
     * @param documentId the ID of the document to update
     * @param loginAccountId the ID of the user requesting the update
     * @param request the request containing the new document name
     */
    public void updateName(Long documentId, Long loginAccountId, DocumentUpdateNameRequest request) {
        documentUseCase.updateName(documentId, loginAccountId, request);
    }

    @Transactional
    public void delete(Long documentId, Long loginAccountId) {
        SaveFileDto saveFileDto = documentUseCase.delete(documentId, loginAccountId);
        documentUseCase.deleteFile(saveFileDto.getUrl());
    }
}
