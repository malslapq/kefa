package com.kefa.application.service;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.contact.command.ContactGetAllCommand;
import com.kefa.api.dto.contact.request.ContactRequest;
import com.kefa.api.dto.contact.request.ContactUpdateStatusRequest;
import com.kefa.api.dto.contact.response.ContactDetailResponse;
import com.kefa.application.usecase.ContactUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactUseCase contactUseCase;

    public ContactDetailResponse add(ContactRequest request) {
        return contactUseCase.add(request);
    }

    public ContactDetailResponse updateStatus(Long contactId, ContactUpdateStatusRequest request) {
        return contactUseCase.updateStatus(contactId, request);
    }

    public Void delete(Long contactId) {
        return contactUseCase.delete(contactId);
    }

    public ContactDetailResponse getDetail(Long contactId) {
        return contactUseCase.getDetail(contactId);
    }

    public PagedResponse<ContactDetailResponse> getAll(ContactGetAllCommand command) {
        return contactUseCase.getAll(command);
    }
}
