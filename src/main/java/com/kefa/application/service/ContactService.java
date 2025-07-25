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

    /**
     * Adds a new contact using the provided request data.
     *
     * @param request the contact creation request containing contact details
     * @return the detailed response of the newly added contact
     */
    public ContactDetailResponse add(ContactRequest request) {
        return contactUseCase.add(request);
    }

    /**
     * Updates the status of a contact identified by the given ID.
     *
     * @param contactId the unique identifier of the contact to update
     * @param request the status update request containing the new status information
     * @return the updated contact details
     */
    public ContactDetailResponse updateStatus(Long contactId, ContactUpdateStatusRequest request) {
        return contactUseCase.updateStatus(contactId, request);
    }

    /**
     * Deletes the contact identified by the given ID.
     *
     * @param contactId the unique identifier of the contact to delete
     * @return null after the contact is deleted
     */
    public Void delete(Long contactId) {
        return contactUseCase.delete(contactId);
    }

    /**
     * Retrieves detailed information for a contact by its unique identifier.
     *
     * @param contactId the unique ID of the contact to retrieve
     * @return the detailed response containing contact information
     */
    public ContactDetailResponse getDetail(Long contactId) {
        return contactUseCase.getDetail(contactId);
    }

    /**
     * Retrieves a paginated list of contact details based on the specified command parameters.
     *
     * @param command the command containing filtering and pagination criteria
     * @return a paged response containing contact detail information
     */
    public PagedResponse<ContactDetailResponse> getAll(ContactGetAllCommand command) {
        return contactUseCase.getAll(command);
    }
}
