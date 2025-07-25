package com.kefa.api.controller;

import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.api.dto.contact.command.ContactGetAllCommand;
import com.kefa.api.dto.contact.request.ContactRequest;
import com.kefa.api.dto.contact.request.ContactUpdateStatusRequest;
import com.kefa.api.dto.contact.response.ContactDetailResponse;
import com.kefa.application.service.ContactService;
import com.kefa.common.response.ApiResponse;
import com.kefa.common.type.ContactStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/contact")
    public ApiResponse<ContactDetailResponse> addContact(@RequestBody @Valid ContactRequest request) {
        return ApiResponse.success(contactService.add(request));
    }

    @GetMapping("/contacts")
    public ApiResponse<PagedResponse<ContactDetailResponse>> getAllContacts(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(required = false) ContactStatus status) {

        ContactGetAllCommand command = ContactGetAllCommand.builder()
            .page(page)
            .size(size)
            .status(status)
            .build();

        return ApiResponse.success(contactService.getAll(command));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'STAFF')")
    @GetMapping("/contacts/{contactId}")
    public ApiResponse<ContactDetailResponse> getContactById(@PathVariable Long contactId) {
        return ApiResponse.success(contactService.getDetail(contactId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'STAFF')")
    @PatchMapping("/contacts/{contactId}/status")
    public ApiResponse<ContactDetailResponse> updateStatus(@PathVariable Long contactId, @RequestBody ContactUpdateStatusRequest request) {
        return ApiResponse.success(contactService.updateStatus(contactId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'STAFF')")
    @DeleteMapping("/contacts/{contactId}")
    public ApiResponse<Void> deleteContact(@PathVariable Long contactId) {
        return ApiResponse.success(contactService.delete(contactId));
    }

}
