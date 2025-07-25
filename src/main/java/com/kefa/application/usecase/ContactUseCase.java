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

    public ContactDetailResponse add(ContactRequest request) {

        Contact contact = Contact.from(request);

        return ContactDetailResponse.from(contactRepository.save(contact));
    }

    @Transactional
    public ContactDetailResponse updateStatus(Long contactId, ContactUpdateStatusRequest request) {

        Contact contact = getContactFromId(contactId);
        contact.updateStatus(request.getStatus());

        return ContactDetailResponse.from(contactRepository.save(contact));
    }

    public Void delete(Long contactId) {
        contactRepository.deleteById(contactId);
        return null;
    }

    @Transactional(readOnly = true)
    public ContactDetailResponse getDetail(Long contactId) {
        return ContactDetailResponse.from(getContactFromId(contactId));
    }

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

    private Contact getContactFromId(Long contactId) {
        return contactRepository.findById(contactId).orElseThrow(() -> new ContactException(ErrorCode.NOT_FOUND_CONTACT));
    }
}
