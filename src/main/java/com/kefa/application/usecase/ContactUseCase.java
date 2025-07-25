package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.contact.command.ContactGetAllCommand;
import com.kefa.api.dto.contact.request.ContactRequest;
import com.kefa.api.dto.contact.request.ContactUpdateStatusRequest;
import com.kefa.api.dto.contact.response.ContactDetailResponse;
import com.kefa.common.exception.ContactException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Contact;
import com.kefa.infrastructure.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactUseCase {

    private final ContactRepository contactRepository;

    /**
     * Creates a new contact from the provided request and returns its detailed response.
     *
     * @param request the data for the new contact
     * @return the detailed response of the created contact
     */
    public ContactDetailResponse add(ContactRequest request) {

        Contact contact = Contact.from(request);

        return ContactDetailResponse.from(contactRepository.save(contact));
    }

    /**
     * Updates the status of a contact identified by its ID and returns the updated contact details.
     *
     * @param contactId the unique identifier of the contact to update
     * @param request the request containing the new status value
     * @return the detailed response of the updated contact
     * @throws ContactException if the contact with the specified ID is not found
     */
    @Transactional
    public ContactDetailResponse updateStatus(Long contactId, ContactUpdateStatusRequest request) {

        Contact contact = getContactFromId(contactId);
        contact.updateStatus(request.getStatus());

        return ContactDetailResponse.from(contactRepository.save(contact));
    }

    /**
     * Deletes the contact with the specified ID.
     *
     * @param contactId the unique identifier of the contact to delete
     * @return always returns null
     */
    public Void delete(Long contactId) {
        contactRepository.deleteById(contactId);
        return null;
    }

    /**
     * Retrieves detailed information for a contact by its ID.
     *
     * @param contactId the unique identifier of the contact
     * @return a detailed response containing the contact's information
     * @throws ContactException if the contact with the specified ID is not found
     */
    @Transactional(readOnly = true)
    public ContactDetailResponse getDetail(Long contactId) {
        return ContactDetailResponse.from(getContactFromId(contactId));
    }

    /**
     * Retrieves a paginated list of contacts, optionally filtered by status.
     *
     * @param command the command containing pagination parameters and an optional status filter
     * @return a paged response containing contact details and pagination metadata
     */
    @Transactional(readOnly = true)
    public PagedResponse<ContactDetailResponse> getAll(ContactGetAllCommand command) {

        Pageable pageable = PageRequest.of(command.getPage(), command.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Contact> contacts = Optional.ofNullable(command.getStatus())
            .map(status -> contactRepository.findByStatus(status, pageable))
            .orElse(contactRepository.findAll(pageable));

        return PagedResponse.<ContactDetailResponse>builder()
            .content(contacts.map(ContactDetailResponse::from).toList())
            .page(contacts.getNumber())
            .size(contacts.getSize())
            .totalElements(contacts.getTotalElements())
            .totalPages(contacts.getTotalPages())
            .build();
    }

    /**
     * Retrieves a contact entity by its ID or throws a ContactException if not found.
     *
     * @param contactId the unique identifier of the contact
     * @return the Contact entity corresponding to the given ID
     * @throws ContactException if no contact is found with the specified ID
     */
    private Contact getContactFromId(Long contactId) {
        return contactRepository.findById(contactId).orElseThrow(() -> new ContactException(ErrorCode.NOT_FOUND_CONTACT));
    }
}
